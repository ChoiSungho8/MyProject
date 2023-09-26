package org.zerock.shop.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 설정 파일이나 Bean을 등록하기 위한 어노테이션
// 엔티티 객체가 생성이 되거나 변경이 되었을 때 @EnableJpaAuditing 어노테이션을 활용하여 자동으로 값을 등록할 수 있습니다.
// ex) 등록일, 수정일, 등록자, 수정자
@EnableJpaAuditing // JPA의 Auditing 기능을 활성화
public class AuditConfig {

    public AuditorAware<String> auditorProvider() { // 등록자와 수정자를 처리해주는 AuditorAware을 빈으로 등록
        return new AuditorAwareImpl();
    }

}
