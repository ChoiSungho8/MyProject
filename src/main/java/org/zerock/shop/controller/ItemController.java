package org.zerock.shop.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.service.ItemService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor // final이나 @NonNull이 있는 필드의 생성자 자동 생성
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

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
        
        try {
            // 조회한 상품 데이터를 모델에 담아서 뷰로 전달
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            // 상품 엔티티가 존재하지 않을 경우 에러메시지를 담아서 상품 등록 페이지로 이동
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        
        return "item/itemForm";        
        
    }

    // 상품을 수정하는 URL 추가
    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto,
                             // @Valid : 들어오는 값에 대해 검증 가능
                             BindingResult bindingResult,
                             // BindingResult : 검증 오류를 보관하는 객체
                             // 쿼리 파라미터를 ModelAttribute 객체로 바인딩하는데에 실패해도 컨트롤러가 호출된다.
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList,
                             // @RequestParam : Http 요청 파라미터 값을 편리하게 사용할 수 있다.
                             // 컨트롤러에서 파라미터 값을 넘겨받을 때 사용
                             Model model) {

        // 오류가 발생하면 상품 등록 페이지로 이동
        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        // 상품 이미지 파일 정보의 첫 번째가 비어있거나 상품 정보의 아이디가 비어있으면
        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫 번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList); // 상품 수정 로직을 호출합니다.
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";

    }

    // 상품 관리 화면 이동 및 조회한 상품 데이터를 화면에 전달하는 로직을 구현
    // 현재 상품 데이터가 많이 없는 관계로 한 페이지당 총 3개의 상품만 보여주도록 하겠습니다.
    // 페이지 번호는 0부터 시작하는 것에 유의
    // value에 상품 관리 화면 진입 시 URL에 페이지 번호가 없는 경우와 페이지 번호가 있는 경우 2가지를 매핑
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page,
                             Model model) {

        // 페이징을 위해서 PageRequest.of 메소드를 통해 Pageable 객체를 생성
        // 첫 번째 파라미터로는 조회할 페이지 번호, 두 번째 파라미터로는 한 번에 가지고 올 데이터 수를 넣어줍니다.
        // URL 경로에 페이지 번호가 있으면 해당 페이지를 조회하도록 세팅하고, 페이지 번호가 없으면 0페이지를 조회
        // .isPresent() 있으면 true 없으면 false
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);

        // 조회 조건과 페이징 정보를 파라미터로 넘겨서 Page<Item> 객체를 반환 받습니다.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);
        // 조회한 상품 데이터 및 페이징 정보를 뷰에 전달합니다.
        model.addAttribute("items", items);
        // 페이지 전환 시 기존 검색 조건을 유지한 채 이동할 수 있도록 뷰에 다시 전달합니다.
        model.addAttribute("itemSearchDto", itemSearchDto);
        // 상품 관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수입니다.
        // 5로 설정했으므로 최대 5개의 이동할 페이지 번호만 보여줍니다.
        model.addAttribute("maxPage", 5);
        return "item/itemMng";

    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }

}
