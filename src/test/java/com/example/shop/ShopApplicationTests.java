package com.example.shop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ShopApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void listPractice(){
        List<String> list = new ArrayList<>(Arrays.asList("A","B","C"));
        list.add("D");

        for (int i = 0; i < list.toArray().length; i++) {
            if(list.get(i).equals("B")){
                System.out.println(list.get(i));
            }
        }

        for (String s : list) {
            if(s.equals("B")){
                System.out.println(s);
            }
        }

    }


}