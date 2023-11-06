package org.zerock.shop.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.shop.config.security.dto.MemberSecurityDto;
import org.zerock.shop.entity.Member;
import org.zerock.shop.repository.MemberRepository1;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository1 memberRepository1;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: " + username);

        Optional<Member> result = memberRepository1.getWithRoles(username);

        if(result.isEmpty()) { // 해당 아이디를 가진 사용자가 없다면

            throw new UsernameNotFoundException("username not found...");

        }

        Member member = result.get();

        MemberSecurityDto memberSecurityDto = new MemberSecurityDto(
                member.getMid(),
                member.getPassword(),
                member.getEmail(),
                member.isDel(),
                false,
                member.getRoleSet().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .collect(Collectors.toList()));

        log.info("memberSecurityDto");
        log.info(memberSecurityDto);

        return memberSecurityDto;

    }

}
