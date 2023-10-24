package org.zerock.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardListAllDto {

    private Long bno;

    private String title;

    private String writer;

    private LocalDateTime regTime;

    private Long replyCount;

    private List<BoardImageDto> boardImages;

}
