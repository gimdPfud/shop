package com.example.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = {"item"})
@NoArgsConstructor
public class Image {
    /*사진 저장 시 사진이 달려있는 참조하고있는 테이블
    * 사진 저장된 경로 경로는 경로+이미지이름(uuid)
    * 사진 이름(uuid없음)*/

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String imgName;
    /*uuid가 포함된 파일 이름*/

    private String oriImgName;
    /*짱구 라는 이미지 이름*/
    private String imgUrl; /*이미지 경로?*/

    private String repimgYn;/*대표이미지 여부*/

    /*참조대상*/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
}