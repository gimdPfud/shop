package com.example.shop.entity;

import com.example.shop.constant.Orderstatus;
import com.example.shop.entity.base.BaseEntity;
import com.example.shop.entity.base.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Orders extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "members_num")
    private Members members;

    /*주문상태 주문 주문취소*/
    @Enumerated(EnumType.STRING)
    private Orderstatus orderstatus;


    @OneToMany(mappedBy = "orders", cascade = CascadeType.ALL)
    /*연관관계의 주인인 (자식객체foreign key 달아준 것)
    테이블에서 참조하는 부모의 클래스명과 변수명 중 변수.*/
    private List<Orderitem> orderitemList
            = new ArrayList<>();
}
