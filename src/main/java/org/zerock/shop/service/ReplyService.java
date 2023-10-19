package org.zerock.shop.service;

import org.zerock.shop.dto.PageRequestDto;
import org.zerock.shop.dto.PageResponseDto;
import org.zerock.shop.dto.ReplyDto;

public interface ReplyService {
    // CRUD

    // 댓글 등록 처리
    Long register(ReplyDto replyDto);

    // 댓글 (불러오기)읽기 처리
    ReplyDto read(Long rno);

    // 댓글 수정 처리
    void modify(ReplyDto replyDto);

    // 댓글 삭제 처리
    void delete(Long rno);

    // 특정 게시물의 댓글 목록 페이징 처리
    PageResponseDto<ReplyDto> getListOfBoard(Long bno, PageRequestDto pageRequestDto);

}
