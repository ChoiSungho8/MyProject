package org.zerock.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity // SpringSecurityFilterChain이 자동으로 포함
public class SecurityConfig {

    /*@Autowired
    MemberService memberService;*/

    // BCryptPasswordEncoder의 해시 함수를 이용하여 비밀번호를 암호화하여 저장
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // static 디렉터리의 하위 파일은 인증을 무시하도록 설정
    /*@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/replies/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/images/**"))
                .requestMatchers(AntPathRequestMatcher.antMatcher("/img/**"));
    }*/

    // http 요청에 대한 보안을 설정합니다.
    // 페이지 권한 설정, 로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정 작성
    // SecurityFilterChain는 반환 값이 있고 @Bean으로 등록함으로써 컴포넌트 기반의 보안 설정 가능
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin(formLogin -> formLogin
                .loginPage("/members/login")
                .defaultSuccessUrl("/", true)
                .usernameParameter("email")
                .failureUrl("/members/login/error"))
                .logout(logout -> logout
                        .logoutRequestMatcher(AntPathRequestMatcher.antMatcher("/members/logout"))
                        .logoutSuccessUrl("/"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger*/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/replies/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/css/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/js/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/images/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger*/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/img/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/board/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/members/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/item/**")).permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole("ADMIN")
                        .anyRequest().authenticated())
                // 인증 되지 않은 사용자가 리소스에 접근하였을 대 수행되는 핸들러를 등록합니다.
                .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

        return http.build();

       /* http
                // .csrf(이곳에 CSRF 설정을 위한 함수)
                .csrf(AbstractHttpConfigurer::disable)

                // .sessionManagement(이곳에 세션 설정을 위한 함수)
                // session 사용하지 않으므로 무상태설정
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // .authorizeHttpRequest(이곳에 인가 설정을 위한 함수);
                // http.authorizeRequests() : 보안 설정을 하겠다는 의미,
                // http.anyRequest.permitAll() : 어떠한 요청에도 인증을 요구한다는 의미
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests.anyRequest().permitAll()
                );

        return http.build();*/
    }

}
