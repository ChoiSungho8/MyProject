package org.zerock.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.dto.BoardDto;
import org.zerock.shop.dto.PageRequestDto;
import org.zerock.shop.dto.PageResponseDto;
import org.zerock.shop.entity.Board;
import org.zerock.shop.repository.BoardRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor // final or @NonNull이 붙은 필드의 생성자 자동 생성
@Transactional // true = commit, false = rollback
public class BoardServiceImpl implements BoardService {
    
    private final ModelMapper modelMapper;
    
    private final BoardRepository boardRepository;
    
    // 등록 작업 처리
    @Override
    public Long register(BoardDto boardDto) {

        // .map(source, destination)
        // source 와 destination 의 타입을 분석하여 매칭 전략 및 기타 설정값에 따라 일치하는 속성을 결정하게 된다.
        // 그런 다음 결정한 매칭 항목들에 대해 데이터를 매핑하는 것이다.
        Board board = modelMapper.map(boardDto, Board.class);

        // 게시물의 번호를 bno에 저장
        Long bno = boardRepository.save(board).getBno();
        
        // 게시물 번호가 저장된 bno 리턴
        return bno;
        
    }

    // 조회 작업 처리
    @Override
    public BoardDto readOne(Long bno) {

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        BoardDto boardDto = modelMapper.map(board, BoardDto.class);

        return boardDto;

    }

    // 수정 작업 처리
    @Override
    public void modify(BoardDto boardDto) {

        Optional<Board> result = boardRepository.findById(boardDto.getBno());

        Board board = result.orElseThrow();

        board.change(boardDto.getTitle(), boardDto.getContent());

        boardRepository.save(board);

    }

    // 삭제 작업 처리
    @Override
    public void remove(Long bno) {

        boardRepository.deleteById(bno);

    }

    // BoardRepository를 호출하는 기능
    @Override
    public PageResponseDto<BoardDto> list(PageRequestDto pageRequestDto) {

        String[] types = pageRequestDto.getTypes();
        String keyword = pageRequestDto.getKeyword();
        Pageable pageable = pageRequestDto.getPageable("bno");

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);

        // Page<Board>를 List<BoardDto>로 직접 변환하는 코드 작성
        List<BoardDto> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDto.class)).collect(Collectors.toList());

        return PageResponseDto.<BoardDto>withAll()
                .pageRequestDto(pageRequestDto)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();

    }

}
