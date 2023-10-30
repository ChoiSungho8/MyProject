package org.zerock.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.shop.dto.*;
import org.zerock.shop.service.BoardService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {
    // 컨트롤러와 화면 처리

    // 실제 파일 삭제도 이루어지므로 첨부파일 경로를 주입 받는다.
    @Value("${org.zerock.upload.path}") // import 시에 springframework로 시작하는 Value
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public void list(PageRequestDto pageRequestDto, Model model) {

        // 화면에 목록 데이터를 출력
        // PageResponseDto<BoardDto> responseDto = boardService.list(pageRequestDto);

        //PageResponseDto<BoardListReplyCountDto> responseDto = boardService.listWithReplyCount(pageRequestDto);

        PageResponseDto<BoardListAllDto> responseDto = boardService.listWithAll(pageRequestDto);

        log.info(responseDto);

        model.addAttribute("responseDto", responseDto);

    }

    // 등록 처리는 GET 방식으로 화면을 보고 POST 방식으로 처리
    @PreAuthorize("hasRole('USER')")
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
    @PreAuthorize("isAuthenticated()")
    @GetMapping({"/read", "/modify"})
    public void read(Long bno, PageRequestDto pageRequestDto, Model model) {

        BoardDto boardDto = boardService.readOne(bno);

        log.info(boardDto);

        model.addAttribute("dto", boardDto);

    }

    // 수정 처리
    @PreAuthorize("principal.username == #boardDto.writer")
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
    public String remove(BoardDto boardDto, RedirectAttributes redirectAttributes) {

        Long bno = boardDto.getBno();
        log.info("remove post.. " + bno);

        boardService.remove(bno);

        // 게시물이 데이터베이스상에서 삭제되었다면 첨부파일 삭제
        log.info(boardDto.getFileNames());
        List<String> fileNames = boardDto.getFileNames();
        if (fileNames != null && fileNames.size() > 0) {
            removeFiles(fileNames);
        }

        redirectAttributes.addFlashAttribute("result", "removed");

        return "redirect:/board/list";

    }

    public void removeFiles(List<String> files) {

        for (String fileName:files) {

            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);

            String resourceName = resource.getFilename();

            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());

                resource.getFile().delete();

                // 섬네일이 존재한다면
                if (contentType.startsWith("image")) {

                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);

                    thumbnailFile.delete();

                }

            } catch (Exception e) {

                log.error(e.getMessage());

            }

        } // end for

    }

}
