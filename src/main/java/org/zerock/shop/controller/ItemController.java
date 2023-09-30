package org.zerock.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.service.ItemService;

import java.util.List;

@Controller
@RequiredArgsConstructor // final이나 @NotNull이 있는 필드의 생성자 자동 생성
public class ItemController {

    // 상품을 등록하는 url을 ItemController에 추가
    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        // ItemFormDto를 model 객체에 담아서 뷰로 전달
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    // 상품을 등록하는 url을 ItemController에 추가
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList) {
        // 검증하려는 객체 앞에 @Valid 선언, 파라미터로 bindingResult 객체 추가
        // 검사 후 결과는 bindingResult에 담고 bindingResult.hasErrors()를 호출하여 에러가 있으면 아이템 등록 페이지로 이동

        // 상품 등록 시 필수 값이 없다면 다시 상품 등록 페이지로 전환
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        // 상품 등록 시 첫 번째 이미지가 없다면 에러 메시지와 함께 상품 등록 페이지로 전환합니다.
        // 상품의 첫 번째 이미지는 메인 페이지에서 보여줄 상품 이미지로 사용하기 위해서 필수 값으로 지정하겠습니다.
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫 번째 상품 이미지는 필수 입력 값입니다.");
            return "item/itemForm";
        }

        try {
            // 상품 저장 로직을 호출.
            // 매개 변수로 상품 정보와 상품 이미지 정보를 담고 있는 itemImgFileList를 넘겨줍니다.
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        // 상품이 정상적으로 등록되었다면 메인 페이지로 이동
        return "redirect:/";

    }

}
