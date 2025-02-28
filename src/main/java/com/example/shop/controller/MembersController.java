package com.example.shop.controller;

import com.example.shop.dto.MembersDTO;
import com.example.shop.service.MembersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/user")
public class MembersController {
    private final MembersService membersService;

    @GetMapping("/signUp")
    public String signUp (MembersDTO membersDTO){
        return "user/signUp";
    }

    @PostMapping("/signUp")
    public String signUpPost (@Valid MembersDTO membersDTO, BindingResult bindingResult){
        log.info(membersDTO);
        log.info("멤버 컨트롤러 : 회원가입 포스트 진입");

        if(bindingResult.hasErrors()){
            log.info("유효성 검사 에러 발생 !!!!!!! ");
            log.info(bindingResult.getAllErrors());
            return "user/signUp";
        }
        try {
            String name = membersService.signUp(membersDTO);
            log.info(name+"님 가입 성공");
        }catch (IllegalStateException e){
            return "user/signUp";
        }

        return "redirect:/user/signUp";
    }

    @GetMapping("/login")
    public String login (){
        return "user/login";
    }


}