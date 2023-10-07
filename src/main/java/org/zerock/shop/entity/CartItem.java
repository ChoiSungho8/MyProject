package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    // 하나의 장바구니에는 여러 개의 상품을 담을 수 있으므로 다대일 관계 매핑
    // 지연 로딩 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // 장바구니에 담을 상품의 정보를 알아야 하므로 상품 엔티티를 매핑해줍니다.
    // 지연 로딩 설정
    @ManyToOne(fetch = FetchType.LAZY)
    // 하나의 상품은 여러 장바구니의 장바구니 상품으로 담길 수 있으므로 마찬가지로 다대일 관계로 매핑
    @JoinColumn(name = "item_id")
    private Item item;

    // 같은 상품을 장바구니에 몇 개 담을지 저장합니다.
    private int count;

    // 장바구니에 담을 상품 엔티티를 생성하는 메소드와 장바구니에 담을 수량을 증가시켜 주는 메소드
    public static CartItem createCartItem(Cart cart, Item item, int count) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    // 장바구니에 기존에 담겨 있는 상품인데, 해당 상품을 추가로 장바구니에 담을 때
    // 기존 수량에 현재 담을 수량을 더해줄 때 사용할 메소드
    public void addCount(int count) {
        this.count += count;
    }

    // 장바구니에서 상품의 수량을 변경할 경우 실시간으로 해당 회원의 장바구니 상품의 수량도 변경하는 로직
    public void updateCount(int count) {
        this.count = count;
    }

}
