package org.zerock.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.entity.Member;
import org.zerock.shop.repository.MemberRepository;

import java.util.Optional;

@Service
// 비즈니스 로직을 담당하는 서비스 계층 클래스에 선언
// 로직 처리 에러 발생 시 변경된 데이터를 로직 수행 이전 상태로 콜백
@Transactional
// 빈 주입 방법 1. @Autowired, 2. 필드 주입(@Setter), 3. 생성자 주입
// final이나 @NonNull이 붙은 필드에 생성자 생성
// 빈에 생성자가 1개이고 생성자의 파라미터 타입이 빈으로 등록이 가능하다면
// @Autowired 없이 의존성 주입 가능
@RequiredArgsConstructor
// MemberService가 UserDetailService를 구현
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Optional<Member> result = memberRepository.findByEmail(member.getEmail());
        Member findMember = result.orElseThrow();

        if (findMember != null) {
            // 이미 가입된 회원의 경우 IllegalStateException 예외 발생
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    // UserDetailService 인터페이스의 loadUserByUsername() 메소드를 오버라이딩
    // 로그인 할 유저의 email을 파라미터로 전달 받음
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> result = memberRepository.findByEmail(email);
        Member member = result.orElseThrow();

        if (member == null) {
            throw new UsernameNotFoundException(email);
        }

        // UserDetail을 구현하고 있는 User 객체를 반환
        // User 객체를 생성하기 위해서 생성자로 회원의 이메일, 비밀번호, role을 파라미터로 넘겨줌.
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }

}
