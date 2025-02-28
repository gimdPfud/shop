package com.example.shop.repository;

import com.example.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemSearch {

    /*이메일을 받음*/
    public Page<Item> search(String[] types, String keyword, String email, Pageable pageable);

    /*이메일 안받음*/
    public Page<Item> mainlist(String[] types, String keyword, String searchDateType, Pageable pageable);

}