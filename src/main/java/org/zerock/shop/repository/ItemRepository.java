package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // JpaRepository는 기본적인 CRUD 및 페이징 처리를 위한 메소드가 정의 되어 있다.



}
