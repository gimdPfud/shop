package com.example.shop.repository;

import com.example.shop.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    public List<Image> findByItem_Id(Long item_id);

    public Image findByItemIdAndRepimgYn(Long item_id, String y);
    /*select * from image where item_id=:item_id and repimgyn=:y*/
}