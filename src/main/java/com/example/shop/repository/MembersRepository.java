package com.example.shop.repository;

import com.example.shop.entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembersRepository extends JpaRepository<Members, Long> {
    //email로 정보 찾기 세션에는 이메일이 저장되기 때문에..
    public Members findByEmail (String email);
}