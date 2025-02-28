package com.example.shop.dto;

import com.example.shop.constant.ItemSellStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter@Setter@ToString@NoArgsConstructor
public class ItemDTO {
    private Long id;            //상품 코드 pk

    @NotBlank(message = "상품명은 필수 입력값 입니다.")
    private String itemNm;      //상품명

    @NotNull(message = "재고수량은 필수 입력값입니다.")
    @PositiveOrZero(message = "가격은 0보다 커야합니다.")
    private int price;          //가격

    @NotNull(message = "재고수량은 필수 입력값입니다.")
    @PositiveOrZero(message = "수량은 0보다 커야합니다.")
    private int stockNumber;    //재고수량

    @NotBlank(message = "상품 상세설명은 필수 입력값 입니다.")
    private String itemDetail;  //상품 상세설명

    //상품 판매상태 대기 판매중/품절
    private ItemSellStatus itemSellStatus;

    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    private String createBy;
    private String modifiedBy;

    private List<ImageDTO> imageDTOList;

    public ItemDTO setImageDTOList(List<ImageDTO> imageDTOList) {
        this.imageDTOList = imageDTOList;
        return this;
    }
}