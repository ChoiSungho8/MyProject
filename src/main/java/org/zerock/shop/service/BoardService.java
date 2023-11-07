package org.zerock.shop.service;

import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.dto.*;
import org.zerock.shop.entity.Board;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
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

    // 게시글의 이미지와 댓글의 숫자까지 처리
    PageResponseDto<BoardListAllDto> listWithAll(PageRequestDto pageRequestDto);

    // Dto를 엔티티로 변환하기
    default Board dtoToEntity(BoardDto boardDto) {

        Board board = Board.builder()
                .bno(boardDto.getBno())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .writer(boardDto.getWriter())
                .build();

        if(boardDto.getFileNames() != null) {
            boardDto.getFileNames().forEach(fileName -> {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            });
        }

        return board;

    }

    // Board 엔티티 객체를 BoardDto 타입으로 변환하는 처리
    default BoardDto entityToDto(Board board) {

        BoardDto boardDto = BoardDto.builder()
                .bno(board.getBno())
                .title(board.getTitle())
                .content(board.getContent())
                .writer(board.getWriter())
                .regTime(board.getRegTime())
                .updateTime(board.getUpdateTime())
                .build();

        List<String> fileNames =
                board.getImageSet().stream().sorted().map(boardImage ->
                        boardImage.getUuid() + "_" + boardImage.getFileName())
                        .collect(Collectors.toList());

        boardDto.setFileNames(fileNames);

        return boardDto;

    }

}
