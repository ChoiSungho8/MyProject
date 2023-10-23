package org.zerock.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.shop.dto.PageRequestDto;
import org.zerock.shop.dto.PageResponseDto;
import org.zerock.shop.dto.ReplyDto;
import org.zerock.shop.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor // 의존성 주입을 위한
public class ReplyController {

    private final ReplyService replyService;

    @Operation(summary = "Replies POST", description = "POST 방식으로 댓글 등록")
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> register(@Valid @RequestBody ReplyDto replyDto,
                                      BindingResult bindingResult) throws BindException {

        log.info(replyDto);

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();

        Long rno = replyService.register(replyDto);

        resultMap.put("rno", rno);

        return resultMap;

    }

    // 특정 게시물의 댓글 목록
    @Operation(summary = "Replies of Board", description = "GET 방식으로 특정 게시물의 댓글 목록")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDto<ReplyDto> getList(@PathVariable("bno") Long bno, PageRequestDto pageRequestDto) {

        PageResponseDto<ReplyDto> responseDto = replyService.getListOfBoard(bno, pageRequestDto);

        return responseDto;

    }

    // 특정 댓글 조회
    @Operation(summary = "Read Reply", description = "GET 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDto getReplyDto( @PathVariable("rno") Long rno ) {

        ReplyDto replyDto = replyService.read(rno);

        return replyDto;

    }

    // 특정 댓글 삭제
    @Operation(summary = "Delete Reply", description = "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove( @PathVariable("rno") Long rno ) {

        replyService.remove(rno);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;

    }

    // 특정 댓글 수정
    @Operation(summary = "Modify Reply", description = "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> modify( @PathVariable("rno") Long rno, @RequestBody ReplyDto replyDto ) {

        replyDto.setRno(rno); // 번호를 일치시킴

        replyService.modify(replyDto);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;

    }

}
