/*
package org.zerock.shop.config.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.shop.config.security.dto.MemberSecurityDto;

import java.io.IOException;

@Log4j2
@RequiredArgsConstructor
public class CustomSocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        log.info("----------------------------------------------------------");
        log.info("CustomLoginSuccessHandler onAuthenticationSuccess ..........");
        log.info(authentication.getPrincipal());

        MemberSecurityDto memberSecurityDto = (MemberSecurityDto) authentication.getPrincipal();

        String encodedPw = memberSecurityDto.getPassword();

        //소셜로그인이고 회원의 패스워드가 1111이라면
        if (memberSecurityDto.isSocial()
                && (memberSecurityDto.getPassword().equals("11111111")
                ||  passwordEncoder.matches("11111111", memberSecurityDto.getPassword())
        )) {
            log.info("Should Change Password");

            log.info("Redirect to Member Modify ");
            response.sendRedirect("/member/modify");

            return;
        } else {

            response.sendRedirect("/board/list");
        }
    }
}
*/
