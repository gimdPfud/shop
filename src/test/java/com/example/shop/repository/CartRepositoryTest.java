package com.example.shop.repository;

import com.example.shop.entity.Cart;
import com.example.shop.entity.Members;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class CartRepositoryTest {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    MembersRepository membersRepository;

    @Test
    void insertTest(){
        Cart cart = cartRepository.findByMembers_Email("tofu@a.a");

        if(cart==null){
            log.info("장바구니 만들 수 있음");
        }else{
            log.info("장바구니 못 만듬");
        }
    }

    @Test
    @Transactional
    void findbyid(){
        Cart cart = cartRepository.findById(1L).get();
        log.info(cart);
    }
}