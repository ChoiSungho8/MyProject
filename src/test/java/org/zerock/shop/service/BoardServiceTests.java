package org.zerock.shop.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.shop.dto.BoardDto;
import org.zerock.shop.dto.PageRequestDto;
import org.zerock.shop.dto.PageResponseDto;

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

        // 변경에 필요한 데이터만
        BoardDto boardDto = BoardDto.builder()
                .bno(101L)
                .title("Updated....101")
                .content("Updated content 101...")
                .build();

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

}
