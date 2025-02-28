package com.example.shop.service;

import com.example.shop.dto.ImageDTO;
import com.example.shop.entity.Image;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@Log4j2
public class FileService {
    @Value("${imgLocation}")
    String imgLocation;

    /*사진 물리적 저장*/
    public ImageDTO register(MultipartFile multipartFile) throws IOException {
        log.info("파일서비스 등록 시작");
        log.info(multipartFile.getOriginalFilename());

        String oriImgName
                = multipartFile.getOriginalFilename()
                .substring(
                        multipartFile.getOriginalFilename()
                                .lastIndexOf("/")+1);
            /*예상값: 짱구.png*/
        log.info("확장자를 포함한 파일의 이름 : "+oriImgName);

        /*uuid를 포함해서 파일명이 겹치지 않게 하기*/
        UUID uuid = UUID.randomUUID();
        /*서로다른 객체를 구별하기 위해 이름 부여할 때 사용, 실제 사용시 중복될 가능성 거의 없음..*/

        /*물리적 파일 이름 하나 만들기*/
        String imgName = uuid.toString() + "_" + oriImgName;

        String fileuploadpath = imgLocation + imgName;

        /*물리적 저장.*/
        FileOutputStream fos = new FileOutputStream(fileuploadpath);
        fos.write(multipartFile.getBytes());

        fos.close();/*자원반납.*/

        ImageDTO imageDTO = new ImageDTO();
        imageDTO.setOriImgName(oriImgName);
        imageDTO.setImgName(imgName);
        imageDTO.setImgUrl(imgLocation);/*나중에 이미지로케이션 밑에~ 어쩌구쩌구폴더*/

        return imageDTO;
    }

    /*사진 물리적 삭제*/
    public void delete(String path){
        /*입력받은 엔티티데이터를 바탕으로 물리적 파일 삭제*/
        /*파일 삭제*/
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }
}