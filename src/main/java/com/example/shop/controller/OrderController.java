package com.example.shop.controller;

import com.example.shop.dto.OrderDTO;
import com.example.shop.exception.OutOfStockException;
import com.example.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order")
    public ResponseEntity order(@RequestBody @Valid OrderDTO orderDTO, BindingResult bindingResult, Principal principal){
        log.info("주문 입장....post");
        if(bindingResult.hasErrors()){
            log.info("유효성 통과 실패!!!!!!!!!!!");
            StringBuilder sb = new StringBuilder();
            /*특징 : append를 통해서 문자열 더하기 문자열이 가능함.*/

            List<FieldError> fieldErrorList = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrorList) {
                sb.append(fieldError.getDefaultMessage());
            }
            log.info("필드에러 메세지 모음...");
            log.info(sb.toString());/*머가 있나 보까~~*/
            return new ResponseEntity(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        if(principal == null){
            log.info("로그인 안되어있는 사용자.");
            return new ResponseEntity<>("", HttpStatus.UNAUTHORIZED);/*todo 나중에... new ResponseEntity<>()*/
        }
        /*주문을 하려면 부모인 오더스엔티티 필요. 멤버와 일대일. 이메일로 주문 찾아오기*/
        String email = principal.getName();

        Long orderid = null;

        try{
            orderid = orderService.order(orderDTO, email);
        } catch (OutOfStockException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        log.info(orderid+"번 주문 저장.");

        /*주문 아이템의 부모인 아이템엔티티. 입력받은 아이템아이디로 해결!!*/

        return new ResponseEntity<Long>(orderid,HttpStatus.OK);
    }
}
