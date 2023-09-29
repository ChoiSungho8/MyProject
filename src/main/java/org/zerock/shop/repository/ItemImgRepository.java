package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.shop.entity.ItemImg;

// 상품의 이미지 정보를 저장하기 위함
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {
}
