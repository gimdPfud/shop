package com.example.shop.service;

import com.example.shop.dto.ImageDTO;
import com.example.shop.entity.Image;
import com.example.shop.entity.Item;
import com.example.shop.repository.ImageRepository;
import com.example.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class ImageService {
    private final ImageRepository imageRepository;
    private ModelMapper modelMapper = new ModelMapper();
    private final FileService fileService;
    private final ItemRepository itemRepository;

    /*사진db저장*/
    public ImageDTO register(MultipartFile multipartFile, Long item_id, String repimgYn) throws IOException {

        /*물리적 사진 저장 후 내용을 바탕으로 DTO를 만들어 반환.*/
        ImageDTO imageDTO = fileService.register(multipartFile);
        /*DB에 저장*/
        Image image = modelMapper.map(imageDTO, Image.class);

        /*참조대상 찾기*/
        Item item = itemRepository.findById(item_id).orElseThrow(EntityNotFoundException::new);
        image.setItem(item);

        /*찾아온 대상이 대표이미지가 있다면..*/
        Image image1 = null;

        if(repimgYn!=null){//대표이미지 있는지 없는지 확인.
            image1 = imageRepository.findByItemIdAndRepimgYn(item.getId(), repimgYn);
        }

        if(image1 != null) {
            /*대표이미지가 있다면... 기존 이미지 삭제!*/
            String oldmainpath = image1.getImgUrl()+image1.getImgName();
            fileService.delete(oldmainpath);
            /*변경한다.*/
            image1.setImgUrl(imageDTO.getImgUrl());
            image1.setImgName(imageDTO.getImgName());
            image1.setOriImgName(imageDTO.getOriImgName());
        }else{
            /*대표이미지가 없거나 상세이미지를 저장해야하는 경우..*/
            image.setRepimgYn(repimgYn);
            image.setItem(item);
            imageRepository.save(image);
            imageDTO = modelMapper.map(image, ImageDTO.class);

        }
        return imageDTO;
    }

    public List<ImageDTO> read(Long item_id){
        /*전체 이미지 가져오기*/
        List<Image> imageList = imageRepository.findByItem_Id(item_id);

        List<ImageDTO> imageDTOList
                = imageList.stream().map(
                        image -> modelMapper.map(image, ImageDTO.class))
                .collect(Collectors.toList());

        return imageDTOList;
    }

    /*삭제*/
    public void delete(Long num){
        /*경로 찾아오기*/
        Image image = imageRepository.findById(num).orElseThrow(EntityNotFoundException::new);

        /*물리적 파일 삭제*/
        if(image!=null) {
            String path = image.getImgUrl() + image.getImgName();
            fileService.delete(path);
            /*db에 데이터 삭제*/
            imageRepository.delete(image);
            /*deleteById보다 쿼리 1개 덜 날아감. (select>delete)(delete)*/
        }
    }
}