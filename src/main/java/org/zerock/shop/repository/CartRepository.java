package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {



}
