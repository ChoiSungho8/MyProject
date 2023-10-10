package org.zerock.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // @Getter, @Setter, @ToString ... 등 꾸러미
// @Builder, @AllArgsConstructor, @NoArgsConstructor 세트
@Builder // 빌더 패턴은 생성자 파라미터가 많을 때 가독성 좋고 순서 상관없이 적용
@AllArgsConstructor // 여기에 필드에 쓴 모든생성자만 만들어줌
@NoArgsConstructor // 기본 생성자를 만들어줌
public class BoardDto {

    private Long bno;

    private String title;

    private String content;

    private String writer;

    private LocalDateTime regDate;

    private LocalDateTime modDate;

}
