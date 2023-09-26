package org.zerock.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// Auditing을 적용하기 위함
@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass // 공통 매핑 정보가 필요할 때
@Getter
public abstract class BaseEntity extends BaseTimeEntity {

    // @CreatedBy를 사용하여 데이터가 생성될 때 유저의 ID가 DB에 저장되게 기능을 구현했다.
    @CreatedBy
    @Column(updatable = false) // 업데이트 시점에 막는 기능
    private String createdBy;

    // @LastModifiedBy를 사용하여 데이터가 수정될 때 유저의 ID가 DB에 저장되게 기능을 구현했다.
    @LastModifiedBy
    private String modifiedBy;

}
