package com.example.shop.dto;

import com.example.shop.entity.Orderitem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter @Getter @ToString @NoArgsConstructor
public class OrderitemDTO {
    /*사용자에게 찍어줄 DTO...*/
    private String itemNm;
    private int count;
    private int orderPrice; //주문금액
    private String imgUrl; // 상품 이미지 경로

    /*생성자*/
    public OrderitemDTO(Orderitem orderitem, String imgUrl) {
        this.itemNm = orderitem.getItem().getItemNm();
        this.count = orderitem.getCount();
        this.orderPrice = orderitem.getItem().getPrice();

        /*item 안에 imageList에서 하나씩 가져와서..(.stream().map()) if image(엔티티) 안의 repimg eq "Y".
        * 여기서 String imgUrl = 이미지엔티티의 컬럼 두 개 합치기.
        * return imgUrl
        * 하면 굳이 String파라미터를 안받아도 됨.*/
        this.imgUrl = imgUrl;
    }
}
