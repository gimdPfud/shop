package com.example.shop.service;

import com.example.shop.constant.Orderstatus;
import com.example.shop.dto.*;
import com.example.shop.entity.*;
import com.example.shop.exception.OutOfStockException;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MembersRepository;
import com.example.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MembersRepository membersRepository;
    private final ItemRepository itemRepository;

    /*주석주석*/

    /*todo 주문*/
    public Long order(OrderDTO orderDTO, String email){
        /*참조될 item엔티티 찾기*/
        Item item = itemRepository.findById(orderDTO.getItemID())
                .orElseThrow(EntityNotFoundException::new);

        /*참조될 members엔티티 찾기*/
        Members members = membersRepository.findByEmail(email);

        /*주문수량만큼 상품재고 변경*/
        if((item.getStockNumber() - orderDTO.getCount())<0) {
            throw new OutOfStockException("상품 재고가 부족합니다. 현재 남은 수량 : "+item.getStockNumber());
        }
        item.setStockNumber(item.getStockNumber() - orderDTO.getCount());

        /*부모인 orders set*/
        Orders orders = new Orders();
        orders.setMembers(members);                 //주문한 회원
        orders.setOrderstatus(Orderstatus.ORDER);   //주문 상태

        /*orders엔티티를 위한 orderitemList를 만든다.*/
        List<Orderitem> orderitemList = new ArrayList<>();
        Orderitem orderitem = new Orderitem();
        orderitem.setItem(item);                    //입력받은 아이템
        orderitem.setCount(orderDTO.getCount());    //입력받은 주문수량
        orderitem.setOrderprice(item.getPrice());   //상품의 가격. 카운트랑 곱하기 안함??
        orderitem.setOrders(orders);                //부모를 set
        orderitemList.add(orderitem);               //주문아이템리스트에 담기.

        orders.setOrderitemList(orderitemList);     //주문아이템리스트

        /*orders 저장하기*/
        Orders result = orderRepository.save(orders);

        return result.getId();
    }

    /*todo 주문내역*/
    public ResponesPageDTO getOrderList(String email, RequestPageDTO requestPageDTO){
        Page<Orders> ordersPage = orderRepository.findOrders(email,requestPageDTO.getPageable("id"));
        /*이메일이 같은 데이터들을, 아이디"id"를 기준으로 페이징 처리해서, 페이지 ordersPage로 만들기.*/

        /*페이지 ordersPage를 리스트로 만들기.*/
        List<Orders> ordersList = ordersPage.getContent();

        /*웹에 찍어주기위한 주문목록DTO 리스트를 만든다.*/
        List<OrderHistDTO> orderHistDTOList = new ArrayList<>();

        for (Orders orders : ordersList) {//DB에서 가져온 주문목록에서 하나씩 읽음.

            /*DB주문목록의 주문 하나를 가져와서 주문내역DTO에 담음.*/
            OrderHistDTO orderHistDTO = new OrderHistDTO(orders); //뷰 페이지로 갈 객체. dtoList.

            /*주문한 상품 리스트orderitemList를 가져온다.*/
            List<Orderitem> orderitemList = orders.getOrderitemList();

            for (Orderitem orderitem : orderitemList) {
                /*주문상품orderitem 안의 > 상품item 안의 > 이미지리스트ImageList를 가져온다.*/
                List<Image> imageList = orderitem.getItem().getImageList();
                //주문한 상품의 모든 사진 가져오기.

                for (Image image : imageList) {
                    if(image.getRepimgYn()!=null && image.getRepimgYn().equals("Y")){
                        /*이미지리스트에서 하나씩 읽어와서 대표이미지만 거름.*/
                        //주문한 상품의 대표이미지 = image

                        /*주문상품DTO로 변환한다!(DTO에서 만들었던 그거.)
                        * 이거 하려고 for문 여러개 깠던듯..*/
                        OrderitemDTO orderitemDTO =
                                new OrderitemDTO(orderitem, image.getImgUrl()+image.getImgName());

                        /*주문내역DTO에 주문아이템DTO도 넣었다.*/
                        orderHistDTO.addOrderitemDto(orderitemDTO);
                        orderHistDTOList.add(orderHistDTO);
                    }
                }
            }
        }

        return new ResponesPageDTO<>(requestPageDTO, orderHistDTOList, (int) ordersPage.getTotalElements());
    }
}
