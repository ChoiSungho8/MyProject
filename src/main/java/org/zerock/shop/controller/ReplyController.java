package org.zerock.shop.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // 특정 게시물 댓글 목록 처리
    @Operation(summary = "Replies of Board", description = "GET 방식으로 특정 게시물의 댓글 목록")
    @GetMapping(value = "/list/{bno}")
    public PageResponseDto<ReplyDto> getList(@PathVariable("bno") Long bno, PageRequestDto pageRequestDto) {

        PageResponseDto<ReplyDto> responseDto = replyService.getListOfBoard(bno, pageRequestDto);

        return responseDto;

    }

}
