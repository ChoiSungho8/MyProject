package org.zerock.shop.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zerock.shop.config.security.APIUserDetailsService;
import org.zerock.shop.config.security.CustomUserDetailsService;
import org.zerock.shop.config.security.filter.APILoginFilter;
import org.zerock.shop.config.security.filter.RefreshTokenFilter;
import org.zerock.shop.config.security.filter.TokenCheckFilter;
import org.zerock.shop.config.security.handler.APILoginSuccessHandler;
import org.zerock.shop.config.security.handler.Custom403Handler;
import org.zerock.shop.config.security.handler.CustomSocialLoginSuccessHandler;
import org.zerock.shop.util.JWTUtil;

import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity // SpringSecurityFilterChain이 자동으로 포함
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // 자동 로그인 구현 DB 생성해야함
    /*create table persistent_logins (
        username varchar(64) not null,
        series varchar(64) primary key,
        token varchar(64) not null,
        last_used timestamp not null
    );*/
    
    private final DataSource dataSource;

    private final CustomUserDetailsService userDetailsService;

    // 주입
    private final APIUserDetailsService apiUserDetailsService;

    private final JWTUtil jwtUtil;

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, APIUserDetailsService apiUserDetailsService) {
        return new TokenCheckFilter(apiUserDetailsService, jwtUtil);
    }

    // BCryptPasswordEncoder의 해시 함수를 이용하여 비밀번호를 암호화하여 저장
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // http 요청에 대한 보안을 설정합니다.
    // 페이지 권한 설정, 로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정 작성
    // SecurityFilterChain는 반환 값이 있고 @Bean으로 등록함으로써 컴포넌트 기반의 보안 설정 가능
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // AuthenticationManager설정
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder
                .userDetailsService(apiUserDetailsService)
                .passwordEncoder(passwordEncoder());

        // Get AuthenticationManager
        AuthenticationManager authenticationManager =
                authenticationManagerBuilder.build();

        // 반드시 필요
        http.authenticationManager(authenticationManager);

        // APILoginFilter
        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken");
        apiLoginFilter.setAuthenticationManager(authenticationManager);

        // APILoginSuccessHandler
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil);
        // SuccessHandler 세팅
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler);

        // APILoginFilter의 위치 조정
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class);

        // api로 시작하는 모든 경로는 TokenCheckFilter 동작
        http.addFilterBefore(
                tokenCheckFilter(jwtUtil, apiUserDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );

        // 이게 있어야 jsonData 값 넘어옴
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        // refreshToken 호출 처리
        http.addFilterBefore(new RefreshTokenFilter("/refreshToken", jwtUtil),
                TokenCheckFilter.class);

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        http.sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.formLogin(formLogin -> formLogin
                .loginPage("/member/login")
                .defaultSuccessUrl("/", true)
                .usernameParameter("email")
                .failureUrl("/member/login/error"))
                .logout(logout -> logout
                        .logoutRequestMatcher(AntPathRequestMatcher.antMatcher("/member/logout"))
                        .logoutSuccessUrl("/"))
                // 자동 로그인
                .rememberMe(rememberMe -> rememberMe
                        .key("12345678")
                        .tokenRepository(persistentTokenRepository())
                        .userDetailsService(userDetailsService)
                        .tokenValiditySeconds(60*60*24*30))
                // 소셜 로그인 설정
                .oauth2Login(oauth -> oauth
                        .loginPage("/member/login")
                        .successHandler(authenticationSuccessHandler()))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/member/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/board/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/item/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/api/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/cart/**")).hasRole("USER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/order/**")).hasRole("USER")
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                        .anyRequest().authenticated())
                // 인증 되지 않은 사용자가 리소스에 접근하였을 대 수행되는 핸들러를 등록합니다.
                // 로그인이 되었어도 권한이 없으면
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        return http.build();

    }

    // 이거 없으면 악명 높은 CORS 에러 메세지 뜸
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new Custom403Handler();
    }

    // OAuth2 로그인 관련 CustomSocialLoginSuccessHandler를 로그인 성공 처리 시 이용
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomSocialLoginSuccessHandler(passwordEncoder());
    }
    
    // static 디렉터리의 하위 파일은 인증을 무시하도록 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger*/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/upload/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/view/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/replies/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/remove/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/images/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/img/**"));
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

}
