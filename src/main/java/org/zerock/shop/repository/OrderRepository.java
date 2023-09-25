package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {



}
