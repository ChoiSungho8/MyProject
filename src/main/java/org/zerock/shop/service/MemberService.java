package org.zerock.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.entity.Member;
import org.zerock.shop.repository.MemberRepository;

@Service
// 비즈니스 로직을 담당하는 서비스 계층 클래스에 선언
// 로직 처리 에러 발생 시 변경된 데이터를 로직 수행 이전 상태로 콜백
@Transactional
// 빈 주입 방법 1. @Autowired, 2. 필드 주입(@Setter), 3. 생성자 주입
// final이나 @NonNull이 붙은 필드에 생성자 생성
// 빈에 생성자가 1개이고 생성자의 파라미터 타입이 빈으로 등록이 가능하다면
// @Autowired 없이 의존성 주입 가능
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if (findMember != null) {
            // 이미 가입된 회원의 경우 IllegalStateException 예외 발생
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

}
