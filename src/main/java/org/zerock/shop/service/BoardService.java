package org.zerock.shop.service;

import org.zerock.shop.dto.BoardDto;

public interface BoardService {

    // 등록 작업 처리
    Long register(BoardDto boardDto);

    // 조회 작업 처리
    BoardDto readOne(Long bno);

    // 수정 작업 처리
    void modify(BoardDto boardDto);

    // 삭제 작업 처리
    void remove(Long bno);

}
