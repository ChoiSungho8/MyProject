package org.zerock.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

// Auditing을 적용하기 위해서 @EntityListeners 어노테이션을 추가
@EntityListeners(value = {AuditingEntityListener.class})
// 공통 매핑 정보가 필요할 때 사용하는 어노테이션으로 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
@MappedSuperclass
@Getter @Setter
public abstract class BaseTimeEntity {

    @CreatedDate // 엔티티가 생성되어 저장될 때 시간을 자동으로 저장
    @Column(updatable = false) //  update 시점에 막는 기능
    private LocalDateTime regTime;
    
    @LastModifiedDate // 엔티티의 값을 변경할 때 시간을 자동으로 저장
    private LocalDateTime updateTime;

}
