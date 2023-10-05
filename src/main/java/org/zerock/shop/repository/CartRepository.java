package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

    // 현재 로그인한 회원의 Cart 엔티티를 찾기 위해서 쿼리 메소드 추가
    Cart findByMemberId(Long memberId);

}
