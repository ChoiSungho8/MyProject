package org.zerock.shop.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zerock.shop.constant.Role;
import org.zerock.shop.entity.Member;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 일반 회원 추가 테스트
    @Test
    public void insertMembers() {

        IntStream.rangeClosed(1, 100).forEach(i -> {

            Member member = Member.builder()
                    .mid("member" + i)
                    .password(passwordEncoder.encode("11111111"))
                    .email("email" + i + "@aaa.bbb")
                    .build();

            member.addRole(Role.USER);

            if (i >= 90) {
                member.addRole(Role.ADMIN);
            }

            memberRepository.save(member);

        });

    }

    // 회원 조회 테스트
    @Test
    public void testRead() {

        Optional<Member> result = memberRepository.getWithRoles("member100");

        Member member = result.orElseThrow();

        log.info(member);
        log.info(member.getRoleSet());

        member.getRoleSet().forEach(role -> log.info(role.name()));

    }

}
