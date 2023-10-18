package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.zerock.shop.entity.ItemImg;

import java.util.List;


// 상품의 이미지 정보를 저장하기 위함
public interface ItemImgRepository extends JpaRepository<ItemImg, Long> {

    List<ItemImg> findByItemIdOrderByIdAsc(@Param("itemId") Long itemId);

    // 상품의 대표 이미지를 찾는 쿼리 메소드
    // 구매 이력 페이지에서 주문 상품의 대표 이미지를 보여주기 위해서 추가
    ItemImg findByItemIdAndRepimgYn(@Param("itemId") Long itemId, @Param("repimgYn") String repimgYn);

}
