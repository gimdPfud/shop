package com.example.shop.service;

import com.example.shop.dto.ImageDTO;
import com.example.shop.dto.ItemDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponesPageDTO;
import com.example.shop.entity.Image;
import com.example.shop.entity.Item;
import com.example.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor @Log4j2
@Transactional
public class ItemService {
    private final ItemRepository itemRepository;
    private final ImageService imageService;
    ModelMapper modelMapper = new ModelMapper();

    /*등록*/
    public ItemDTO register(ItemDTO itemDTO, MultipartFile mainimg , MultipartFile[] multipartFiles) throws IOException {
        log.info("상품 등록 시작 : "+itemDTO);
        Item item = modelMapper.map(itemDTO, Item.class);

        /*본문저장*/
        item = itemRepository.save(item);

        /*사진저장*/
        imageService.register(mainimg, item.getId(), "Y");
        if(multipartFiles!=null){
            for (int i = 0; i < multipartFiles.length; i++) {
                imageService.register(multipartFiles[i], item.getId(), null);
            }
        }

        itemDTO = modelMapper.map(item, ItemDTO.class);
        log.info("상품 등록 끝 : "+itemDTO);
        return itemDTO;
    }

    /*목록*/
    public ResponesPageDTO<ItemDTO> list (String email, RequestPageDTO requestPageDTO){
        log.info("리스폰스페이지DTO 시작 : "+email);
        log.info(requestPageDTO);
        /*정렬조건*/
        Pageable pageable = requestPageDTO.getPageable("id");

        Page<Item> itemPage = itemRepository.search(requestPageDTO.getTypes(),
                requestPageDTO.getKeyword(), email, pageable);

        log.info(itemPage);
        /*Item변환*/
        List<Item> itemList = itemPage.getContent();

        /*디티오 변환*/
        List<ItemDTO> itemDTOList = itemList.stream().map(item->modelMapper.map(item,ItemDTO.class)).collect(Collectors.toList());

        /*총 수*/
        int total = (int) itemPage.getTotalElements();

        /*리스폰스 생성*/
        ResponesPageDTO<ItemDTO> responesPageDTO = new ResponesPageDTO<>(requestPageDTO, itemDTOList, total);

        log.info(responesPageDTO);
        return responesPageDTO;
    }/*목록2*/
    public ResponesPageDTO<ItemDTO> mainlist (RequestPageDTO requestPageDTO){
        Pageable pageable = requestPageDTO.getPageable("id");
        Page<Item> itemPage = itemRepository.mainlist(requestPageDTO.getTypes(), requestPageDTO.getKeyword(), requestPageDTO.getSearchDateType(), pageable);
        log.info(itemPage);
        /*Item변환*/
        List<Item> itemList = itemPage.getContent();

//        /*디티오 변환*/
//        List<ItemDTO> itemDTOList = itemList.stream()
//                .map(item->{
//                    ItemDTO itemDTO = modelMapper.map(item,ItemDTO.class);
//                    List<ImageDTO> imageDTOList = new ArrayList<>();
//                    for (Image image : item.getImageList()) {
//                        ImageDTO imageDTO = modelMapper.map(image,ImageDTO.class);
//                        imageDTOList.add(imageDTO);
//                    }
//                    itemDTO.setImageDTOList(imageDTOList);
//                    return itemDTO;
//                })
//                .collect(Collectors.toList());
        /*디티오 변환*/
        List<ItemDTO> itemDTOList = itemList.stream()
                .map(item->modelMapper.map(item,ItemDTO.class)
                        .setImageDTOList(
                                item.getImageList().stream().map(
                                        image -> modelMapper.map(image,ImageDTO.class))
                                        .collect(Collectors.toList())))
                .collect(Collectors.toList());

        /*총 수*/
        int total = (int) itemPage.getTotalElements();

        /*리스폰스 생성*/
        ResponesPageDTO<ItemDTO> responesPageDTO = new ResponesPageDTO<>(requestPageDTO, itemDTOList, total);

        log.info(responesPageDTO);
        return responesPageDTO;
    }

    /*읽기*/
    public ItemDTO read(Long item_id){
        Item item = itemRepository.findById(item_id).orElseThrow(EntityNotFoundException::new);

        List<ImageDTO> imageDTOList = imageService.read(item_id);

        ItemDTO itemDTO = modelMapper.map(item, ItemDTO.class);

        itemDTO.setImageDTOList(imageDTOList);

        return itemDTO;
    }

    /*수정*/
    public ItemDTO update(ItemDTO itemDTO, MultipartFile[] multipartFile, MultipartFile mainimg, Long[] delino) throws IOException {
        Item item = itemRepository.findById(itemDTO.getId()).orElseThrow(EntityNotFoundException::new);
        /*상품정보수정*/
        item.setPrice(item.getPrice());
        item.setItemNm(itemDTO.getItemNm());
        item.setItemDetail(itemDTO.getItemDetail());
        item.setItemSellStatus(itemDTO.getItemSellStatus());
        item.setStockNumber(itemDTO.getStockNumber());

        /*상품이미지 삭제
        * 배열로 들어온 삭제번호를 이용해서 db에서 삭제, 물리적삭제를 실행.*/
        if(delino!=null && delino.length>0){
            for (Long l : delino) {
                imageService.delete(l);
            }
        }

        /*상품이미지 등록*/
        /*대표이미지*/
        if(mainimg!=null && !mainimg.isEmpty()){
            imageService.register(mainimg, item.getId(), "Y");
        }
        /*상세이미지*/
        if(multipartFile!=null){
            for (int i = 0; i < multipartFile.length; i++) {
                imageService.register(multipartFile[i], item.getId(), null);
            }
        }
        
        return modelMapper.map(item, ItemDTO.class);
    }

    /*삭제*/
    public void del(Long item_id){
        List<ImageDTO> imageDTOList = imageService.read(item_id);

        /*리뷰삭제*/

        /*참조관계인 모든거 추가*/

        /*주문테이블...?*/
    }
}