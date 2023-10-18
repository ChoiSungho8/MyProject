package org.zerock.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.shop.dto.BoardDto;
import org.zerock.shop.dto.BoardListReplyCountDto;
import org.zerock.shop.dto.PageRequestDto;
import org.zerock.shop.dto.PageResponseDto;
import org.zerock.shop.service.BoardService;

@RestController
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {
    // 컨트롤러와 화면 처리

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDto pageRequestDto, Model model) {

        // 화면에 목록 데이터를 출력
        // PageResponseDto<BoardDto> responseDto = boardService.list(pageRequestDto);

        PageResponseDto<BoardListReplyCountDto> responseDto = boardService.listWithReplyCount(pageRequestDto);

        log.info(responseDto);

        model.addAttribute("responseDto", responseDto);

    }

    // 등록 처리는 GET 방식으로 화면을 보고 POST 방식으로 처리
    @GetMapping("/register")
    public void registerGET() {

    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDto boardDto, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        log.info("board POST register.......");

        if (bindingResult.hasErrors()) {
            log.info("has errors........");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            return "redirect:/board/register";
        }

        log.info(boardDto);

        Long bno = boardService.register(boardDto);

        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/board/list";

    }

    // 특정한 번호의 게시물을 조회하는 기능
    @GetMapping({"/read", "/modify"})
    public void read(Long bno, PageRequestDto pageRequestDto, Model model) {

        BoardDto boardDto = boardService.readOne(bno);

        log.info(boardDto);

        model.addAttribute("dto", boardDto);

    }

    // 수정 처리
    @PostMapping("/modify")
    public String modify(PageRequestDto pageRequestDto,
                         @Valid BoardDto boardDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {

        log.info("board modify post......." + boardDto);

        if(bindingResult.hasErrors()) {
            log.info("has errors......");

            String link = pageRequestDto.getLink();

            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());

            return "redirect:/board/modify?"+link;
        }

        boardService.modify(boardDto);

        redirectAttributes.addFlashAttribute("result", "modified");

        redirectAttributes.addAttribute("bno", boardDto.getBno());

        return "redirect:/board/read";

    }

    // 삭제 처리
    @PostMapping("/remove")
    public String remove(Long bno, RedirectAttributes redirectAttributes) {

        log.info("remove post.. " + bno);

        boardService.remove(bno);

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";

    }

}
