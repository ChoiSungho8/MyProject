package org.zerock.shop.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;
import org.zerock.shop.repository.OrderItemRepository;
import org.zerock.shop.repository.OrderRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional // 특정 실행단위에서 오류 발생시 전체 실행 내용을 롤백해주는 기능
public class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @PersistenceContext // EntityManager를 Bean으로 주입할 때 씀
    EntityManager em;

    public Item createItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL); // SELL = 판매중
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now()); // 현재 시간 가져오기
        item.setUpdateTime(LocalDateTime.now()); // 첫 등록이므로 수정 날짜도 현재 시간
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {
        Order order = new Order();

        for(int i=0; i<3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item); // 상품은 위에 만든 상품
            orderItem.setCount(10); // 상품 수량
            orderItem.setOrderPrice(1000); // 상품 가격
            orderItem.setOrder(order); // 주문하고 주문상품에 저장
            order.getOrderItems().add(orderItem);
            // 아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order 엔티티에 담아줍니다.

        }

        orderRepository.saveAndFlush(order); // order 엔티티를 저장하면서 강제로 flush를 호출하여
        // 영속성 컨텍스트에 있는 객체들을 데이터베이스에 반영합니다.

        em.clear(); // 영속성 컨텍스트의 상태를 초기화합니다.

        // itemOrder 엔티티 3개가 실제로 데이터베이스에 저장되었는지 검사합니다.
        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size()); // 두 객체의 값이 같은지 비교

    }

    public Order createOrder() {
        Order order = new Order();

        for(int i=0; i<3; i++) {
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item); // 주문상품에 상품 넣고
            orderItem.setCount(10); // 개수 넣고
            orderItem.setOrderPrice(1000); // 주문 가격 생성
            orderItem.setOrder(order); // order 값을 orderItem에 넣김
            order.getOrderItems().add(orderItem);
            // 아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order 엔티티에 담아줌
            // 영속성 컨텐스트란 엔티티를 영구 저장하는 환경이라는 뜻이다.
        }

        Member member = new Member(); // Member 객체 생성
        memberRepository.save(member); // memberRepository에 member 객체 저장
        
        order.setMember(member); // Member에 있는 member 값을 order에 넣어줌
        orderRepository.save(order); // 저장
        return order; // order 리턴

    }
    
    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest() {
        Order order = this.createOrder(); // createOrder(값 생성 후) order에 넣기
        order.getOrderItems().remove(0);
        // order 엔티티에서 관리하고 있는 orderItem 리스트의 0번째 인덱스 요소를 제거합니다.
        em.flush(); // 영속성 컨텍스트에 저장 후 데이터베이스에 반영
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
        Order order = this.createOrder(); // 주문 생성 메소드를 이용하여 주문 데이터 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        // 영속성 컨텍스트의 상태 초기화 후 order 엔티티에 저장했던 주문 상품 아이디를 이용하여
        // orderItem을 데이터베이스에서 다시 조회합니다.
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);

        // Order 클래스 조회 결과가 HibernateProxy라고 출력됨
        // 지연 로딩으로 설정하면 실제 엔티티 대신에 프록시 객체를 넣어줌
        System.out.println("Order class : " + orderItem.getOrder().getClass());
        // orderItem 엔티티에 있는 order 객체의 클래스를 출력합니다.
        // Order 클래스가 출력되는 것을 확인할 수 있습니다.
        // 출력 결과 : Order class : class com.shop.entity.Order

        System.out.println("=========================");
        // 프록시 객체는 실제로 사용되기 전까지 데이터 로딩을 하지 않고, 실제 사용 시점에 조회 쿼리문이 실행
        // Order의 주문일(orderDate)을 조회할 때 select 쿼리문이 실행
        orderItem.getOrder().getOrderDate();
        System.out.println("=========================");

    }

}
