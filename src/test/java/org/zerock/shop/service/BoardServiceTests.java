package org.zerock.shop.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.shop.dto.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest // 테스트하기 위한 통합 설정
@Log4j2 // log 출력
public class BoardServiceTests {

    @Autowired // 생성자 생성
    private BoardService boardService;

    // 등록 테스트
    @Test
    public void testRegister() {

        log.info(boardService.getClass().getName());

        BoardDto boardDto = BoardDto.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        Long bno = boardService.register(boardDto);

        log.info("bno : " + bno);

    }

    // 수정 테스트
    @Test
    public void testModify() {

        // 변경에 필요한 데이터
        BoardDto boardDto = BoardDto.builder()
                .bno(101L)
                .title("Updated....101")
                .content("Updated content 101...")
                .build();

        // 첨부파일을 하나 추가
        boardDto.setFileNames(Arrays.asList(UUID.randomUUID() + "_zzz.jpg"));

        boardService.modify(boardDto);

    }

    // 목록/검색 기능 확인
    @Test
    public void testList() {

        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .type("tcw")
                .keyword("1")
                .page(1)
                .size(10)
                .build();

        PageResponseDto<BoardDto> responseDto = boardService.list(pageRequestDto);

        log.info(responseDto);

    }

    @Test
    public void testRegisterWithImages() {

        log.info(boardService.getClass().getName());

        BoardDto boardDto = BoardDto.builder()
                .title("File...Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        boardDto.setFileNames(
                Arrays.asList(
                        UUID.randomUUID() + "_aaa.jpg",
                        UUID.randomUUID() + "_bbb.jpg",
                        UUID.randomUUID() + "_ccc.jpg"
                ));

        Long bno = boardService.register(boardDto);

        log.info("bno : " + bno);

    }

    // 조회 시에 Board와 BoardImage들을 같이 처리하는지 확인
    @Test
    public void testReadAll() {

        Long bno = 101L;

        BoardDto boardDto = boardService.readOne(bno);

        log.info(boardDto);

        for (String fileName : boardDto.getFileNames()) {

            log.info(fileName);

        } // end for

    }

    // 게시물 삭제 처리
    // Board 클래스에 CascadeType.ALL과 orphanRemoval 속성값 true로 지정되어 있음
    // 게시물 삭제시 BoardImage 객체들도 같이 삭제
    @Test
    public void testRemoveAll() {

        Long bno = 1L;

        boardService.remove(bno);

    }

    // 목록 데이터 테스트
    @Test
    public void testListWithAll() {

        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDto< BoardListAllDto> responseDto = boardService.listWithAll(pageRequestDto);

        List<BoardListAllDto> dtoList = responseDto.getDtoList();

        dtoList.forEach(boardListAllDto -> {

            log.info(boardListAllDto.getBno() + " : " + boardListAllDto.getTitle());

            if(boardListAllDto.getBoardImages() != null) {
                for (BoardImageDto boardImage : boardListAllDto.getBoardImages()) {
                    log.info(boardImage);
                }
            }
            log.info("------------------------");
        });

    }

}
