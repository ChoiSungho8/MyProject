package org.zerock.shop.util;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Log4j2
public class JWTUtilTests {

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    public void testGenerate() {

        Map<String, Object> claimMap = Map.of("mid", "ABCDE");

        String jwtStr = jwtUtil.generateToken(claimMap, 1);

        log.info(jwtStr);

    }

    @Test
    public void testValidate() {

        // 유효 시간이 지난 토큰 (ExpiredJwtException 예외 발생)
        String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTkyNTE3OTcsIm1pZCI6IkFCQ0RFIiwiaWF0IjoxNjk5MjUxNzM3fQ.qeHg7tn03GWTNj2ysCgZUNHTKqBq--XS9kfgfgp-4Ak";

        // 고의로 문자열의 마지막 부분에 임의의 문자를 추가 (SignatureException 예외 발생)
        //String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTkyNTE3OTcsIm1pZCI6IkFCQ0RFIiwiaWF0IjoxNjk5MjUxNzM3fQ.qeHg7tn03GWTNj2ysCgZUNHTKqBq--XS9121351353kfgfgp-4Ak";

        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);

        log.info(claim);

    }

    @Test
    public void testAll() {

        String jwtStr = jwtUtil.generateToken(Map.of("mid", "AAAA", "email", "aaaa@bbb.com"), 1);

        log.info(jwtStr);

        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);

        log.info("MID : " + claim.get("mid"));

        log.info("EMAIL : " + claim.get("email"));


    }

}
