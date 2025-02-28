package com.example.shop.service;

import com.example.shop.dto.MembersDTO;
import com.example.shop.entity.Members;

public interface MembersService {
    //회원가입
    public String signUp(MembersDTO membersDTO);

    //로그인
}