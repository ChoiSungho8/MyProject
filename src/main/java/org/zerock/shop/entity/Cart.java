package org.zerock.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "cart")
@Getter
@Setter
@ToString
public class Cart extends BaseEntity {

    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)// 회원 엔티티와 일대일로 매핑합니다. 지연 로딩 설정.
    @JoinColumn(name="member_id") // 매핑할 외래키 지정, name 속성에는 매핑할 외래키의 이름 설정
    // name을 명시하지 않으면 JPA가 알아서 ID를 찾지만 컬럼며이 원하는 대로 생성되지 않을 수 있기 때문에 직접 지정
    private Member member;

}
