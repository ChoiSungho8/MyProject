package org.zerock.shop.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.QItem;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 통합 테스트를 위해 스프링 부트에서 제공하는 어노테이션
@TestPropertySource(locations="classpath:application-test.properties")
// application.properties랑 같은 설정이 있다면 더 높은 우선순위 부여
class ItemRepositoryTest {

    @Autowired // ItemRepository를 사용하기 위해 Bean 주입
    ItemRepository itemRepository;

    // 영속성 컨텍스트를 사용하기 위해 @PersistenceContext 어노테이션을 이용해
    // EntityManager 빈을 주입합니다.
    @PersistenceContext
    EntityManager em;

    @Test // 테스트할 메소드 위에 선언하여 해당 메소드를 테스트 대상으로 지정
    @DisplayName("상품 저장 테스트") // 테스트 코드 실행 시 지정한 테스트명 노출
    public void createItemTest() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem.toString());
    }

    @Test
    public void createItemList() {

        for(int i=1; i<=10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }

    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmTest() {
        this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("상품명, 상품상세설명 or 테스트")
    public void findByItemNmOrItemDetailTest() {
        this.createItemList();
        // 기존에 만들었던 테스트 상품을 만드는 메소드를 실행하여 조회할 대상을 만듦
        List<Item> itemList = itemRepository
                .findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        // itemNm(상품명)이 "테스트 상품1", itemDetail(상품 상세 설명)이 "테스트 상품 상세 설명5"인 상품
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDeatailTest() {
        this.createItemList(); // 값을 생성하고
        // itemList에 itemDetail(상품 상세 설명)으로 찾은 값을 넣는다.
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for(Item item : itemList) { // item에 itemList 값을 넣고
            System.out.println(item.toString()); // item을 문자열로 출력
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemDetailByNative() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for(Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest() {
        this.createItemList(); // 값 생성

        // JPAQueryFactory를 이용하여 쿼리를 동적으로 생성
        // 생성자의 파라미터로는 EntityManager 객체를 넣어줌
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        // Querydsl을 통해 쿼리를 생성하기 위해 플러그인을 통해 자동으로 생성된 QItem 객체를 이용
        QItem qItem = QItem.item;

        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        // fetch를 이용하여 query결과를 리스트로 반환
        // fetch() 메소드 실행 시점에 쿼리문이 실행
        List<Item> itemList = query.fetch();

        for(Item item : itemList) {
            System.out.println(item.toString());
        }

    }

    // 상품 데이터를 만드는 새로운 메소드를 생성
    public void createItemList2() {
        for(int i=1; i<=5; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL); // 판매상태 SELL(판매 중)
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }

        for(int i=6; i<=10; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SOLD_OUT); // 판매상태 SOLD_OUT(품절)
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2() {

        this.createItemList2();
        
        // 쿼리에 들어갈 조건을 만들어주는 빌더
        // Predicate를 구현하고 있으며 메소드 체인 형식으로 사용할 수 있음
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        QItem item = QItem.item;

        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        // and 조건 추가
        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price)); // price(10003)보다 가격이 큰 값

        // 판매상태가 SELL일 때 조건을 동적으로 추가
        if(StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        // 페이지 처리 (조회할 페이지의 번호(0), 한 페이지당 조회할 데이터의 개수(5))
        Pageable pageable = PageRequest.of(0, 5);

        // QueryDslPredicateExecutor 인터페이스에서 정의한 findAll() 메소드를 이용해
        // 조건에 맞는 데이터를 Page객체로 받는다.
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPagingResult.getTotalElements());

        List<Item> resultItemList = itemPagingResult.getContent();
        for(Item resultItem : resultItemList) {
            System.out.println(resultItem.toString());
        }

    }

}