package com.example.shop.repository;

import com.example.shop.config.CustomModelMapper;
import com.example.shop.constant.Orderstatus;
import com.example.shop.dto.ImageDTO;
import com.example.shop.dto.ItemDTO;
import com.example.shop.entity.*;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest @Log4j2
class OrderRepositoryTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    MembersRepository membersRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Test
    void insertTest(){
        /*주문을 등록하려면 부모인 멤버 필요*/
        Members members = membersRepository.findByEmail("tofu@a.a");

        Orders orders = new Orders();
        orders.setMembers(members);
        orders.setOrderstatus(Orderstatus.ORDER);

        orderRepository.save(orders);
    }

    @Test
    void iteminsert(){
        /*어떤 아이템을 사는가?*/
        Item item = itemRepository.findById(1L).get();

        /*누구를 참조?*/
        Orders orders = orderRepository.findById(1L).get();
        Orderitem orderitem = new Orderitem();
        orderitem.setOrders(orders);
        /*참조하는 부모엔티티 추가*/
        orderitem.setItem(item);
        orderitem.setCount(2);
        orderitem.setOrderprice(item.getPrice()*orderitem.getCount());
        orderItemRepository.save(orderitem);
    }

    @Test
    @Transactional
    void readtest(){
        /*양방향 OneToMany*/
        Orders orders
                = orderRepository.findById(1L).get();
//        log.info(orders);
        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.map(orders.getOrderitemList().get(0), ItemDTO.class);
        /*이럼 어떡해 ?.. 오더는 있는데 오더아이템리스트는 없음.*/
    }

    @Test
    @Transactional
    void modelconfigtest(){
        ModelMapper modelMapper = new ModelMapper();
        ModelMapper modelMapper1 = new CustomModelMapper();
//        log.info(modelMapper1.map(null, ItemDTO.class));

    }

    @Test
    void mapper2(){
        modelMapper.map(null, ImageDTO.class);
    }

    @Test
    @Transactional
    @Rollback(value = false)/*테스트는 기본 롤백된다.*/
    void cascadeTest(){
        /*주문을 등록하려면 부모인 멤버 필요*/
        Members members = membersRepository.findByEmail("tofu@a.a");

        Orders orders = new Orders();
        orders.setMembers(members);
        orders.setOrderstatus(Orderstatus.ORDER);

        /*주문 테이블에 주문 아이템을 같이 가져간다.*/
        Item item = itemRepository.findById(1L).get();
        Orderitem orderitem = new Orderitem();
        orderitem.setItem(item);
        orderitem.setCount(7);
        orderitem.setOrderprice(item.getPrice()*orderitem.getCount());
        //부모는 없다. 이제 있음.
        orderitem.setOrders(orders);

        List<Orderitem> orderitemList = new ArrayList<>();
        orderitemList.add(orderitem);/*상품 추가*/
        orderitemList.add(orderitem);/*상품 추가*/

        /*부모엔티티에 자식엔티티를 넣어준다.*/
        orders.setOrderitemList(orderitemList);

        /*부모만 저장한다.*/
        orderRepository.save(orders);
    }

    @Test
    @Transactional
    void findOrdersTest(){
        Pageable pageable = PageRequest.of(0,10);
        Page<Orders>  ordersPage =
        orderRepository.findByMembersEmail("tofu@a.a",pageable);

        ordersPage.forEach(orders -> log.info(orders));
    }
}