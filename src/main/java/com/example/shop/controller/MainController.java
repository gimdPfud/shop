package com.example.shop.controller;

import com.example.shop.dto.ItemDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponesPageDTO;
import com.example.shop.entity.Item;
import com.example.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Log4j2
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping("/")
    public String mainPage(RequestPageDTO requestPageDTO, Model model){

        ResponesPageDTO<ItemDTO> responesPageDTO = itemService.mainlist(requestPageDTO);

        model.addAttribute("responesPageDTO", responesPageDTO);

        return "main";
    }

    @GetMapping("/item/read")
    public String read(RequestPageDTO requestPageDTO,Long id, Model model, RedirectAttributes redirectAttributes){
        log.info("상품구매페이지 진입");
        log.info(requestPageDTO);
        if(id==null){
            return "redirect:/";
        }
        try {/*정보 가져오기*/
            ItemDTO itemDTO = itemService.read(id);
            log.info("가져온 상품 보기 : "+itemDTO);
            model.addAttribute("itemDTO", itemDTO);
            return "item/itemOrder";
        }catch (EntityNotFoundException e){
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/";
        }
    }
}