package com.example.shop.dto;

import com.example.shop.constant.Orderstatus;
import com.example.shop.entity.Orders;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter @ToString @NoArgsConstructor
public class OrderHistDTO {
    /*필드*/
    private Long orderId;
    private String orderDate;
    private Orderstatus orderstatus;
    /*주문 상품 리스트*/
    private List<OrderitemDTO> orderitemDTOList = new ArrayList<>();

    /*생성자*/
    public OrderHistDTO(Orders orders) {
        /*기존 생성자는 파라미터를 하나씩 받아서 만들었지만~~ 객체를 받아서 알아서 값을 할당.(이방법 좋은듯 ㅎ)*/
        this.orderId = orders.getId();
        this.orderDate = orders.getRegTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderstatus = orders.getOrderstatus();
        //이렇게 말고 생성하고나서 modelMapper로 대체해도 가능하디.
    }

    /*method*/
    public void addOrderitemDto(OrderitemDTO orderitemDTO){
        orderitemDTOList.add(orderitemDTO);
    }
}
