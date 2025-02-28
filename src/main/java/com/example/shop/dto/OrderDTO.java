package com.example.shop.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString @NoArgsConstructor
public class OrderDTO {

    @NotNull(message = "수량은 필수 입력값입니다.")
    @Positive(message = "1개 이상 주문해야 합니다.")
    private int count;

    private Long itemID;/*이건 item(참조)를 위한것..*/
}
