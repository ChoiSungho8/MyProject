package org.zerock.shop.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDto {

    private Long bno;
    private String title;
    private String writer;
    private LocalDateTime regTime;

    private Long replyCount;

}
