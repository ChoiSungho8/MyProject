package org.zerock.shop.config;

import jakarta.servlet.DispatcherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.zerock.shop.service.MemberService;

@Configuration
@EnableWebSecurity // SpringSecurityFilterChain이 자동으로 포함
public class SecurityConfig {

    @Autowired
    MemberService memberService;

    // http 요청에 대한 보안을 설정합니다.
    // 페이지 권한 설정, 로그인 페이지 설정, 로그아웃 메소드 등에 대한 설정 작성
    // SecurityFilterChain는 반환 값이 있고 @Bean으로 등록함으로써 컴포넌트 기반의 보안 설정 가능
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
        ;

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/members/**"),
                                new AntPathRequestMatcher("/item/**"),
                                new AntPathRequestMatcher("/images/**")
                        ).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                        .anyRequest().authenticated());

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;

        return http.build();


        /*http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
        ;

        http.authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().denyAll()
        );

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        ;

        return http.build();*/

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

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(new AntPathRequestMatcher( "/css/**"))
                .requestMatchers(new AntPathRequestMatcher( "/js/**"))
                .requestMatchers(new AntPathRequestMatcher( "/img/**"));
    }

    // BCryptPasswordEncoder의 해시 함수를 이용하여 비밀번호를 암호화하여 저장
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
