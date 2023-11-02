package org.zerock.shop.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zerock.shop.config.security.handler.Custom403Handler;
import org.zerock.shop.config.security.handler.CustomSocialLoginSuccessHandler;

import javax.sql.DataSource;

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
