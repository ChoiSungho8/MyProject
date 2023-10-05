package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
// 정렬할 때 사용하는 "order" 키워드가 있기 때문에 "orders"로 지정
@Table(name = "orders")
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    // 한 명의 회원은 여러 번 주문을 할 수 있으므로
    // 주문 엔티티 기준에서 다대일 단방향 매핑을 합니다.

    private LocalDateTime orderDate; // 주문일

    // enum 타입을 엔티티 속성으로 지정
    // Enum 사용 시 순서가 저장되는데, 순서가 바뀔 경우 문제가 발생할 수 있으므로 String 저장 권장
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문상태

    // 주문 상품 엔티티와 일대다 매핑.
    // 외래키(order_id)가 order_item 테이블에 있으므로 연관 관계의 주인은 OrderItem 엔티티입니다.
    // Order 엔티티가 주인이 아니므로 "mappedBy" 속성으로 연관 관계의 주인을 설정합니다.
    // 속성의 값으로 "order"를 적어준 이유는 OrderItem에 있는 Order에 의해 관리된다는 의미로 해석
    // 즉, 연관 관계의 주인의 필드인 order를 mappedBy의 값으로 세팅
    // orphanRemoval 고아 객체 제거
    // 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeTypeAll 옵션을 설정
    // 하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    //private LocalDateTime regTime;

    //private LocalDateTime updateTime;

    // 생성한 주문 상품 객체를 이용하여 주문 객체를 만드는 메소드를 작성
    
    // orderItems에는 주문 상품 정보들을 담고 orderItem 객체를 order 객체의 orderItems에 추가
    // Order 엔티티와 OrderItem 엔티티가 양방향 참조 관계 이므로, orderItem 객체에도 order 객체를 세팅합니다.
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {

        Order order = new Order();
        // 상품을 주문한 회원의 정보를 세팅합니다.
        order.setMember(member);
        // 상품 페이지에서는 1개의 상품을 주문하지만, 장바구니 페이지에서는 한 번에 여러 개의 상품을 주문할 수 있습니다.
        // 따라서 여러 개의 주문 상품을 담을 수 있도록 리스트형태로 파라미터 값을 받으며 주문 객체에 orderItem 객체를 추가합니다.
        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        // 주문 상태를 "ORDER"로 세팅합니다.
        order.setOrderStatus(OrderStatus.ORDER);
        // 현재 시간을 주문 시간으로 세팅합니다.
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // 총 주문 금액을 구하는 메소드
    public int getTotalPrice() {
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    // 주문 취소 시 주문 수량을 상품의 재고에 더해주는 로직과 주문 상태를 취소 상태로 바꿔주는 메소드를 구현합니다.
    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;

        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }

}
