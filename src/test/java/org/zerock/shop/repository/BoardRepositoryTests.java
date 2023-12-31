package org.zerock.shop.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.dto.BoardDto;
import org.zerock.shop.dto.BoardListAllDto;
import org.zerock.shop.dto.BoardListReplyCountDto;
import org.zerock.shop.dto.PageRequestDto;
import org.zerock.shop.entity.Board;
import org.zerock.shop.entity.BoardImage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    /*@Test
    public void testInsert() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title("title..." + i)
                    .content("content..." + i)
                    .writer("user" + (i % 10))
                    .build();

            Board result = boardRepository.save(board);
            log.info("BNO : " + result.getBno());
        });
    }*/

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1, 100).forEach(i -> {
            Board board = Board.builder()
                    .title("Board..." + i)
                    .content("Board...content..." + i)
                    .writer("user" + (i % 10))
                    //.dueDate(LocalDate.of(2023, (i%12)+1, (i%30)+1))
                    .complete(false)
                    .build();

            boardRepository.save(board);

        });
    } // end method

    @Test
    public void testSelect() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        log.info(board);
    }

    @Test
    public void testUpdate() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        board.change("update..title 100", "update content 100");

        boardRepository.save(board);
    }

    @Test
    public void testDelete() {
        Long bno = 1L;

        boardRepository.deleteById(bno);
    }

    @Test
    public void testPaging() {

        // 1 page order by bno desc
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count : " + result.getTotalElements());
        log.info("total pages : " + result.getTotalPages());
        log.info("page number : " + result.getNumber());
        log.info("page size : " + result.getSize());

        List<Board> boardList = result.getContent();

        boardList.forEach(board -> log.info(board));

    }

    // 검색 조건에 대한 테스트
    @Test
    public void testSearch() {

        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .from(LocalDate.of(2023,10,01))
                .to(LocalDate.of(2023,12,31))
                .build();

        Page<BoardDto> result = boardRepository.list(pageRequestDto);

        result.forEach(boardDto -> log.info(boardDto));

    }

    @Test
    public void testSearch1() {

        // 2 page order by bno desc
        Pageable pageable = PageRequest.of(1, 10, Sort.by("bno").descending());

        boardRepository.search1(pageable);

    }

    @Test
    public void testSearchAll() {

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

    }

    // PageImpl을 이용한 Page<T> 반환
    // List<T> : 실제 목록 데이터
    // Pageable : 페이지 관련 정보를 가진 객체
    // long : 전체 개수
    @Test
    public void testSearchAll2() {

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        // total pages
        log.info("total pages : " + result.getTotalPages());

        // page size
        log.info("page size : " + result.getSize());

        // page number
        log.info("page number : " + result.getNumber());

        // prev next
        log.info("prev : " + result.hasPrevious() + ", next : " + result.hasNext());

        result.getContent().forEach(board -> log.info(board));

    }

    @Test
    public void testSearchReplyCount() {

        String[] types = {"t", "c", "w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        Page< BoardListReplyCountDto> result = boardRepository.searchWithReplyCount(types, keyword, pageable);

        // total pages
        log.info(result.getTotalPages());

        // page size
        log.info(result.getSize());

        // pageNumber
        log.info(result.getNumber());

        // prev next
        log.info("prev : " + result.hasPrevious() + ", next : " + result.hasNext());

        result.getContent().forEach(board -> log.info(board));

    }

    @Test
    public void testInsertWithImages() {

        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();

        for (int i = 0; i < 3; i++) {

            board.addImage(UUID.randomUUID().toString(), "file" + i + ".jpg");

        } // end for

        boardRepository.save(board);

    }

    @Test
    public void testReadWithImages() {

        // 반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("--------------------");
        for (BoardImage boardImage : board.getImageSet()) {
            log.info(boardImage);
        }

    }

    // orphanRemoval 속성
    @Transactional
    @Commit
    @Test
    public void testModifyImages() {

        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        // 기존의 첨부파일들은 삭제
        board.clearImages();

        // 새로운 첨부파일들
        for (int i = 0; i < 2; i++) {

            board.addImage(UUID.randomUUID().toString(), "updatefile" + i + ".jpg");

        }

        boardRepository.save(board);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {

        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);

    }

    // 테스트를 위한 더미 데이터의 추가
    @Test
    public void testInsertAll() {

        for (int i = 1; i <= 100; i++) {

            Board board = Board.builder()
                    .title("Title.." + i)
                    .content("Content.." + i)
                    .writer("writer.." + i)
                    .build();

            for (int j = 0; j < 3; j++) {

                if(i % 5 == 0) {
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(), i + "file" + j + ".jpg");
            }
            boardRepository.save(board);
        } // end for

    }

    @Transactional
    @Test
    public void testSearchImageReplyCount() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("bno").descending());

        //boardRepository.searchWithAll(null, null, pageable);

        Page<BoardListAllDto> result = boardRepository.searchWithAll(null, null, pageable);

        log.info("-------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDto -> log.info(boardListAllDto));

    }

}
