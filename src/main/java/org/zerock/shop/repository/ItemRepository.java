package org.zerock.shop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.zerock.shop.entity.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>,
        QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    // JpaRepository는 기본적인 CRUD 및 페이징 처리를 위한 메소드가 정의 되어 있다.

    // itemNm(상품명)으로 데이터를 조회, 엔티티명은 생략이 가능(findItemByItemNm)
    List<Item> findByItemNm(String itemNm);
    
    // 상품명과 상품 상세 설명을 OR 조건을 이용하여 조회하는 쿼리 메소드
    List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);

    // 파라미터로 넘어온 price 변수보다 값이 작은(LessThan) 상품 데이터를 조회하는 쿼리 메소드
    List<Item> findByPriceLessThan(Integer price);
    
    // price보다 작은 상품 데이터를 조회하고 price로 내림차순 정렬(가격이 높은 순으로 출력)
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
