package com.example.shop.repository;

import com.example.shop.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    /*내가 주문한 주문 목록을 찾아오는 쿼리~
    * 현재 주문과 주문 아이템은 양방향 참조관계.
    * 그래서 주문을 가져오면 주문 아이템이 따라온다.(Join)*/
    @Query("select o from Orders o where o.members.email = :email")
    public Page<Orders> findOrders (String email, Pageable pageable);

    /*똑같은것..*/
    public Page<Orders> findByMembersEmail (String email, Pageable pageable);
}
