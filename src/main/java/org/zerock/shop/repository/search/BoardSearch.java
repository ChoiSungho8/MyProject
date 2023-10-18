package org.zerock.shop.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.shop.dto.BoardListReplyCountDto;
import org.zerock.shop.entity.Board;

public interface BoardSearch {

    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

    Page<BoardListReplyCountDto> searchWithReplyCount(String[] types, String keyword, Pageable pageable);

}
