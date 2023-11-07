package org.zerock.shop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data // @Getter, @Setter, @ToString ... 등 꾸러미
// @Builder, @AllArgsConstructor, @NoArgsConstructor 세트
@Builder // 빌더 패턴은 생성자 파라미터가 많을 때 가독성 좋고 순서 상관없이 적용
@AllArgsConstructor // 여기에 필드에 쓴 모든생성자만 만들어줌
@NoArgsConstructor // 기본 생성자를 만들어줌
public class BoardDto {

    private Long bno;

    // 제목, 내용, 작성자가 비어 있지 않도록 @NotEmpty 설정
    @NotEmpty
    @Size(min = 3, max = 100)
    private String title;

    @NotEmpty
    private String content;

    @NotEmpty
    private String writer;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate dueDate;

    private boolean complete;

    // 첨부파일의 이름들
    private List<String> fileNames;

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

}
