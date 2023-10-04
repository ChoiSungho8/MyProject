package org.zerock.shop.service;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Member;
import org.zerock.shop.entity.Order;
import org.zerock.shop.entity.OrderItem;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;
import org.zerock.shop.repository.OrderRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional // true = commit, false = rollback
// 테스트 하기 위한 properties 우선 순위 설정
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderServiceTest {

    @Autowired // 의존성 주입을 할 때 사용하는 Annotation으로 의존 객체의 타입에 해당하는 bean을 찾아 주입하는 역할을 한다.
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    // 테스트를 위해서 주문할 상품과 회원 정보를 저장하는 메소드를 생성

    // 테스트를 위해서 주문할 상품을 저장하는 메소드를 생성
    public Item saveItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    // 테스트를 위해서 회원 정보를 저장하는 메소드를 생성
    public Member saveMember() {
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    // 주문 기능 구현이 잘 되는지 확인
    @Test
    @DisplayName("주문 테스트")
    public void order() {
        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();
        // 주문할 상품과 상품 수량을 orderDto 객체에 세팅
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        // 주문 로직 호출 결과 생성된 주문 번호를 orderId 변수에 저장
        Long orderId = orderService.order(orderDto, member.getEmail());

        // 주문 번호를 이용하여 저장된 주문 정보를 조회합니다.
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        // 주문한 여러 상품들을 리스트로 보관
        List<OrderItem> orderItems = order.getOrderItems();

        // 주문한 상품의 총 가격을 구합니다.
        int totalPrice = orderDto.getCount() * item.getPrice();
        
        // 주문한 상품의 총 가격과 데이터베이스에 저장된 상품의 가격을 비교하여 같으면 테스트가 성공적으로 종료
        assertEquals(totalPrice, order.getTotalPrice());
    }

}
