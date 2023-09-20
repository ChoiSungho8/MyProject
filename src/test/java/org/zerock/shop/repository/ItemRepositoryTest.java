package org.zerock.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.entity.Item;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // 통합 테스트를 위해 스프링 부트에서 제공하는 어노테이션
@TestPropertySource(locations="classpath:application-test.properties")
// application.properties랑 같은 설정이 있다면 더 높은 우선순위 부여
class ItemRepositoryTest {

    @Autowired // ItemRepository를 사용하기 위해 Bean 주입
    ItemRepository itemRepository;

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

}