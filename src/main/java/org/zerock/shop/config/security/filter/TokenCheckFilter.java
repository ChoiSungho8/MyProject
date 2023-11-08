package org.zerock.shop.config.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.shop.config.security.APIUserDetailsService;
import org.zerock.shop.config.security.exception.AccessTokenException;
import org.zerock.shop.util.JWTUtil;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final APIUserDetailsService apiUserDetailsService;
    private final JWTUtil jwtUtil;

    // Access Token을 검증하는 validateAccessToken() 메소드를 추가
    // 예외 종류에 따라서 AccessTokenException으로 처리
    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {

        String headerStr = request.getHeader("Authorization");

        if (headerStr == null || headerStr.length() < 8) {

            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);

        }

        // Bearer 생략
        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7);

        if (tokenType.equalsIgnoreCase("Bearer") == false) {

            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);

        }

        try {
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);

            return values;

        } catch (MalformedJwtException malformedJwtException) {

            log.error("MalformedJwtException-----------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);

        } catch (SignatureException signatureException) {

            log.error("SignatureException---------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);

        } catch (ExpiredJwtException expiredJwtException) {

            log.error("ExpiredJwtException-----------------------");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);

        }

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!path.startsWith("/api/")) {

            filterChain.doFilter(request, response);
            return;

        }

        log.info("Token Check Filter.........................");
        log.info("JWTUtil : " + jwtUtil);

        try {

            Map<String, Object> payload = validateAccessToken(request);

            // mid
            String mid = (String)payload.get("mid");

            log.info("mid : " + mid);

            UserDetails userDetails = apiUserDetailsService.loadUserByUsername(mid);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (AccessTokenException accessTokenException) {

            accessTokenException.sendResponseError(response);

        }

    }

}
