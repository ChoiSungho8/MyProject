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
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    // orphanRemoval 고아 객체 제거
    // 부모 엔티티의 영속성 상태 변화를 자식 엔티티에 모두 전이하는 CascadeTypeAll 옵션을 설정
    // 하나의 주문이 여러 개의 주문 상품을 갖으므로 List 자료형을 사용해서 매핑
    private List<OrderItem> orderItems = new ArrayList<>();

    //private LocalDateTime regTime;

    //private LocalDateTime updateTime;

}
