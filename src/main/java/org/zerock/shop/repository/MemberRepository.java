package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.shop.entity.Member;

import java.util.Optional;

// public interface MemberRepository extends JpaRepository<Member, Long>
public interface MemberRepository extends JpaRepository<Member, String> {

    // 회원 가입 시 중복된 회원이 있는지 검사하기 위해서
    // 이메일로 회원을 검사할 수 있도록 쿼리 메소드를 작성
    @EntityGraph(attributePaths = "roleSet")
    Optional<Member> findByEmail(String email);

    @EntityGraph(attributePaths = "roleSet")
    @Query("select m from Member m where m.mid = :mid and m.social = false")
    Optional<Member> getWithRoles(@Param("mid") String mid);

    @Modifying
    @Transactional
    @Query("update Member m set m.password = :password where m.mid = :mid")
    void updatePassword(@Param("password") String password, @Param("mid") String mid);
}
