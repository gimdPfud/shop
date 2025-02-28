package com.example.shop.repository;

import com.example.shop.entity.Orderitem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<Orderitem, Long> {
}
