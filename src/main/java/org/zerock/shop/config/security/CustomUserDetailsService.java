/*
package org.zerock.shop.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.shop.config.security.dto.MemberSecurityDto;
import org.zerock.shop.entity.Member;
import org.zerock.shop.repository.MemberRepository;

import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: " + username);

//        UserDetails userDetails = User.builder().username("user1").password(passwordEncoder.encode("1111"))
//                .authorities("ROLE_USER")
//                .build();
//
//        return userDetails;

        Optional<Member> result = memberRepository.getWithRoles(username);

        if(result.isEmpty()) {

            throw new UsernameNotFoundException("username not found");
        }

        Member member = result.get();

        MemberSecurityDto memberSecurityDto = new MemberSecurityDto(
                member.getEmail(),
                member.getPassword(),
                member.isDel(),
                false,
                member.getRoleSet().stream().map(memberRole -> new SimpleGrantedAuthority("ROLE_" + memberRole.name())).collect(Collectors.toList())
        );

        log.info("memberSecurityDTO---------");
        log.info(memberSecurityDto);

        return memberSecurityDto;
    }

}
*/
