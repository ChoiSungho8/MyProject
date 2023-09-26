package org.zerock.shop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.zerock.shop.dto.ItemFormDto;

@Controller
public class ItemController {

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model) {
        // ItemFormDto를 model 객체에 담아서 뷰로 전달
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

}
