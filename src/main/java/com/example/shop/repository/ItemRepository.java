package com.example.shop.repository;

import com.example.shop.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> , ItemSearch {
//    DAO : jdbc
//    mapper : 마이바티스
//    repository : jpa

    //자신이 판매하고있는 상품목록 보기
    public List<Item> findByCreateBy(String email);


    //읽기 기능 조건 : 상품명으로 검색
    public List<Item> findByItemNm(String itemNm);
    public List<Item> findByItemNmContaining(String itemNm);

    public List<Item> findByPriceGreaterThanEqual(int price);

    //상품명과 상품 상세설명으로 검색
    public List<Item> findByItemNmOrItemDetail(String itemNm, String itemDetail);
    //like문을 이용해보자~~
    public List<Item> findByItemNmContainingOrItemDetailContaining(String itemNm, String itemDetail);

    @Query("select i from Item i where i.itemNm like concat('%',:keyword,'%') or i.itemDetail like '%'||:keyword||'%' ")
    public List<Item> selectItemNmDetail(String keyword);

    @Query("select i from Item i where i.itemNm like concat('%',:itemNm,'%') or i.itemDetail like '%'||:itemDetail||'%' ")
    public List<Item> selectItemNmDetail(String itemNm, String itemDetail);
}