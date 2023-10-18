package org.zerock.shop.service;

import org.zerock.shop.dto.BoardDto;
import org.zerock.shop.dto.BoardListReplyCountDto;
import org.zerock.shop.dto.PageRequestDto;
import org.zerock.shop.dto.PageResponseDto;

public interface BoardService {

    // 등록 작업 처리
    Long register(BoardDto boardDto);

    // 조회 작업 처리
    BoardDto readOne(Long bno);

    // 수정 작업 처리
    void modify(BoardDto boardDto);

    // 삭제 작업 처리
    void remove(Long bno);

    // list()라는 이름으로 목록/검색 기능을 선언
    PageResponseDto<BoardDto> list(PageRequestDto pageRequestDto);

    // 댓글의 숫자까지 처리
    PageResponseDto<BoardListReplyCountDto> listWithReplyCount(PageRequestDto pageRequestDto);

}
