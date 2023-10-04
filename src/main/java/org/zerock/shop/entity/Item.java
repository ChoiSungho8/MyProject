package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.exception.OutOfStockException;

@Entity
@Table(name="item")
@Getter @Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // 상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; // 상품명

    @Column(name="price", nullable = false)
    private int price; // 가격

    @Column(nullable = false)
    private int stockNumber; // 재고수량

    @Lob // default 값 = 255
    @Column(nullable = false)
    private String itemDetail; // 상품 상세 설명

    // EnumType.ORDINAL : enum 순서 값을 DB에 저장
    @Enumerated(EnumType.STRING) // enum 이름을 DB에 저장
    private ItemSellStatus itemSellStatus; // 상품 판매 상태

    //private LocalDateTime regTime; // 등록 시간

    //private LocalDateTime updateTime; // 수정 시간

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm(); // 상품 이름
        this.price = itemFormDto.getPrice(); // 상품 가격
        this.stockNumber = itemFormDto.getStockNumber(); // 상품 재고 수량
        this.itemDetail = itemFormDto.getItemDetail(); // 상품 상세 설명
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        // 상품 현재 상태(SELL : 판매중, SOLD_OUT : 매진)
    }

    // 상품을 주문할 경우 상품의 재고를 감소시키는 로직 작성
    // 엔티티 클래스 안에 비즈니스 로직을 메소드로 작성하면
    // 코드의 재사용과 데이터의 변경 포인트를 한군데로 모을 수 있다는 장점이 있다.
    public void removeStock(int stockNumber) {
        // 상품의 재고 수량에서 주문 후 남은 재고 수량을 구합니다.
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            // 상품의 재고가 주문 수량보다 적을 경우 재고 부족 예외를 발생시킵니다.
            throw new OutOfStockException("상품의 재고가 부족 합니다.(현재 재고 수량 : " + this.stockNumber + "개)");
        }
        // 주문 후 남은 재고 수량을 상품의 현재 재고 값으로 할당합니다.
        this.stockNumber = restStock;
    }

}
