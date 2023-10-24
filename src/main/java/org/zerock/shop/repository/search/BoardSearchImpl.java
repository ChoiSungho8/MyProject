package org.zerock.shop.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.shop.dto.BoardImageDto;
import org.zerock.shop.dto.BoardListAllDto;
import org.zerock.shop.dto.BoardListReplyCountDto;
import org.zerock.shop.entity.Board;
import org.zerock.shop.entity.QBoard;
import org.zerock.shop.entity.QReply;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {

    public BoardSearchImpl() {
        super(Board.class);
    }

    // Q도메인을 이용한 쿼리 작성 및 테스트
    @Override
    public Page<Board> search1(Pageable pageable) {

        QBoard board = QBoard.board; // Q도메인 객체

        JPQLQuery<Board> query = from(board); // select.. from board

        // query.where(board.title.contains("1")); // where title like ...

        // Querydsl로 검색 조건과 목록 처리
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(board.title.contains("11")); // title like ...

        booleanBuilder.or(board.content.contains("11")); // content like ....

        query.where(booleanBuilder);
        query.where(board.bno.gt(0L));

        // paging Querydsl로 Pageable 처리 하기
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        return null;
    }

    // searchAll의 반복문과 제어문을 이용한 처리가 가능
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {

        // QBoard 객체 생성
        QBoard board = QBoard.board;
        JPQLQuery<Board> query = from(board);

        if ( (types != null && types.length > 0) && keyword != null ) { // 검색 조건과 키워드가 있다면

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for (String type : types) {

                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }

            } // end for
            query.where(booleanBuilder);
        } // end if

        // bno > 0
        query.where(board.bno.gt(0L));

        // paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();

        long count = query.fetchCount();

        return new PageImpl<>(list, pageable, count);

    }

    @Override
    public Page<BoardListReplyCountDto> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));

        query.groupBy(board);

        if ( (types != null && types.length > 0) && keyword != null ) {

            BooleanBuilder booleanBuilder = new BooleanBuilder();

            for (String type: types) {

                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }

            } // end for

            query.where(booleanBuilder);

        }

        // bno > 0
        query.where(board.bno.gt(0L));

        JPQLQuery<BoardListReplyCountDto> dtoQuery = query.select(Projections.bean(BoardListReplyCountDto.class,
                board.bno,
                board.title,
                board.writer,
                board.regTime,
                reply.count().as("replyCount")
        ));

        this.getQuerydsl().applyPagination(pageable, dtoQuery);

        List<BoardListReplyCountDto> dtoList = dtoQuery.fetch();

        long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);

    }

    @Override
    public Page<BoardListAllDto> searchWithAll(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board)); // left join

        // 검색 조건 추가
        // querydsl을 이용해서 페이징 처리하기 전에 검색 조건과 키워드를 사용하는 부분의 코드를 추가        
        if( (types != null && types.length > 0) && keyword != null ) {
            
            BooleanBuilder booleanBuilder = new BooleanBuilder();
            
            for (String type : types) {
                
                switch (type) {
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
                
            } // end for
            boardJPQLQuery.where(booleanBuilder);
        }
        
        boardJPQLQuery.groupBy(board);

        getQuerydsl().applyPagination(pageable, boardJPQLQuery); // paging

        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDto> dtoList = tupleList.stream().map(tuple -> {
            
            Board board1 = (Board) tuple.get(board);
            long replyCount = tuple.get(1, Long.class);

            BoardListAllDto dto = BoardListAllDto.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regTime(board1.getRegTime())
                    .replyCount(replyCount)
                    .build();

            // BoardImage를 BoardImageDto 처리할 부분
            List<BoardImageDto> imageDtos = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDto.builder()
                                    .uuid(boardImage.getUuid())
                                    .fileName(boardImage.getFileName())
                                    .ord(boardImage.getOrd())
                                    .build()
                    ).collect(Collectors.toList());

            dto.setBoardImages(imageDtos); // 처리된 BoardImageDto들을 추가

            return dto;

        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, totalCount);

    }

}
