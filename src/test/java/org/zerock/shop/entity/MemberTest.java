package org.zerock.shop.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.repository.MemberRepository;

@SpringBootTest // 통합 테스트 제공
@Transactional // 테스트 하고 나서 커밋을 안 할 시 자동 롤백
// 테스트를 할 때 application-test.properties를 쓰기 위함
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberTest {

    @Autowired // 생성자 생성
    MemberRepository memberRepository;

    @PersistenceContext // 엔티티 매니저를 빈으로 주입
    EntityManager em;

    @Test // 테스트 메소드 지정
    @DisplayName("Auditing 테스트") // 테스트 이름을 텍스트로 지정
    // 스프링 시큐리티에서 제공하는 어노테이션
    // @WithMockUser에 지정한 사용자가 로그인한 상태라고 가정하고 테스트를 진행할 수 있다.
    @WithMockUser(username = "gildong", roles = "USER")
    public void auditingTest() {
        Member newMember = new Member(); // Member 객체 생성
        memberRepository.save(newMember); // Member 객체 저장

        // JPA는 영속성 컨텍스트에 데이터를 저장 후 트랜잭션이 끝날 때 flush()를 호출하여 데이터베이스에 반영한다.
        // 회원 엔티티를 영속성 컨텍스트에 저장 후 엔티티 매니저로부터 강제로 flush()를 호출하여
        // 데이터베이스에 반영합니다.
        em.flush();
        // 영속성 컨텍스트에 캐시된 모든 엔티티를 제거하고, 1차 캐시를 비우는 역할을 수행
        // 이후에 다시 엔티티를 사용할 때는 데이터베이스로부터 다시 로딩하게 된다.
        // 값이 잘 들어갔는지 확인하기 위해 깨끗하게 클리어
        em.clear();

        Member member = memberRepository.findByEmail(newMember.getEmail());
        /*Member member = memberRepository.findById(newMember.getId())
                .orElseThrow(EntityNotFoundException::new);*/

        // id를 가져오지 못하면(값이 없으면) 예외처리 하겠다.
        System.out.println("register time : " + member.getRegTime());
        System.out.println("update time : " + member.getUpdateTime());
        System.out.println("create member : " + member.getCreatedBy());
        System.out.println("modify member : " + member.getModifiedBy());

    }

}
