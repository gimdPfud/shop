package com.example.shop.dto;

import com.example.shop.entity.Item;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ImageDTO {
    private Long id;

    private String imgName;
    /*uuid가 포함된 파일 이름*/

    private String oriImgName;
    /*짱구 라는 이미지 이름*/
    private String imgUrl; /*이미지 경로?*/

    private String repimgYn;/*대표이미지 여부*/

    private ItemDTO itemDTO;
}