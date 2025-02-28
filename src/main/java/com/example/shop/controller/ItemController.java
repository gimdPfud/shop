package com.example.shop.controller;

import com.example.shop.dto.ItemDTO;
import com.example.shop.dto.RequestPageDTO;
import com.example.shop.dto.ResponesPageDTO;
import com.example.shop.entity.Item;
import com.example.shop.entity.Members;
import com.example.shop.repository.ItemRepository;
import com.example.shop.repository.MembersRepository;
import com.example.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/admin/item")
public class ItemController {

    private final ItemService itemService;
    private final MembersRepository membersRepository;

    @GetMapping("/register")
    public String register(Principal principal, ItemDTO itemDTO){

//        if(principal==null){
//            return "redirect:/user/login";
//        }
//        Members members
//                = membersRepository.findByEmail(principal.getName());
//
//        if(members==null){
//            return "redirect:/user/login";
//        }
//        if(!members.getRole().name().equals("ADMIN")){
//            return "redirect:/aaaaa";
//        }
        return "item/register";
    }
    @PostMapping("/register")
    public String registerPost(@Valid ItemDTO itemDTO, BindingResult bindingResult,
                               RedirectAttributes redirectAttributes, MultipartFile mainimg , MultipartFile[] multipartFile,
                               Model model){
        /*todo 주의주의주의!!!!!! 여러개 받을라면 @param 꼭 넣어야 함/ 근데 그냥 배열 []로 받으면 띨롱 적어도 됨.*/
        log.info("저장하기 : "+itemDTO);

        /*들어온 파일 보기 로그*/
        if(multipartFile !=null){
            for (MultipartFile file : multipartFile) {
                log.info(file);
                log.info(file.getName());
                log.info(file.getOriginalFilename());
            }
        }

        if(mainimg !=null){
            log.info(mainimg);
            log.info(mainimg.getName());
            log.info(mainimg.getOriginalFilename());
        }

        if(bindingResult.hasErrors()){
            log.info("유효성 검사 오류 !!!");
            log.info(bindingResult.getAllErrors());
            return "item/register";
        }

        /*대표이미지는 꼭 있어야한다.*/
        if(mainimg==null || mainimg.isEmpty()){
            log.info("이미지 없음");
            model.addAttribute("msg", "대표 이미지는 꼭 있어야 합니다.");
            return "item/register";
        }

        /*본문저장*//*사진저장*/
        try {
            itemDTO = itemService.register(itemDTO, mainimg, multipartFile);
        }catch (IOException e){
            model.addAttribute("msg", "이미지 저장에 실패했습니다.");
            return "item/register";
        }


        redirectAttributes.addFlashAttribute("itemNm", itemDTO.getItemNm());
        log.info("저장한 DTO : "+itemDTO);
        return "redirect:/admin/item/list";
    }

    @GetMapping("/list")
    public String list(RequestPageDTO requestPageDTO, Model model, Principal principal){
        log.info("상품 목록 페이지");
        log.info(requestPageDTO);
        /*아이템 서비스에서 list불러오기*/
        ResponesPageDTO<ItemDTO> responesPageDTO = itemService.list(principal.getName(), requestPageDTO);

        /*model을 통해서 데이터 보내기*/
        model.addAttribute("responesPageDTO", responesPageDTO);

        log.info("보내는 데이터 : "+responesPageDTO);
        /*view페이지로 보내기*/
        return "item/list";
    }

    @GetMapping("/read")
    public String read(Long id, Model model, Principal principal, RedirectAttributes redirectAttributes){
        log.info("상품정보페이지");
        if(id==null){
            return "redirect:/admin/item/list";
        }
        try {/*정보 가져오기*/
            ItemDTO itemDTO = itemService.read(id);
            log.info("가져온 상품 보기 : "+itemDTO);
            if (!itemDTO.getCreateBy().equals(principal.getName())) {
                log.info("작성자와 불일치. 리스트로 이동");
                log.info(itemDTO.getCreateBy());
                log.info(principal.getName());
                return "redirect:/admin/item/list";
            }

            model.addAttribute("itemDTO", itemDTO);
            return "item/read";
        }catch (EntityNotFoundException e){
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/admin/item/list";
        }
    }

    @GetMapping("/update")
    public String updateGet(Long id, Model model, Principal principal, RedirectAttributes redirectAttributes, ItemDTO itemDTO){
        log.info("수정페이지진입");
        if(id==null){
            return "redirect:/admin/item/list";
        }
        try {/*정보 가져오기*/
            itemDTO = itemService.read(id);
            log.info("가져온 상품 보기 : "+itemDTO);
            if (!itemDTO.getCreateBy().equals(principal.getName())) {
                log.info("작성자와 불일치. 리스트로 이동");
                log.info(itemDTO.getCreateBy());
                log.info(principal.getName());
                return "redirect:/admin/item/list";
            }

            model.addAttribute("itemDTO", itemDTO);
            return "item/update";
        }catch (EntityNotFoundException e){
            redirectAttributes.addFlashAttribute("msg", "존재하지 않는 상품입니다.");
            return "redirect:/admin/item/list";
        }
    }

    @PostMapping("/update")
    public String updatePost(ItemDTO itemDTO, MultipartFile mainimg, MultipartFile[] multipartFile, Long[] delino){
        log.info("수정 post 진입");
        log.info("아이템DTO : "+itemDTO);
        log.info(mainimg);
        log.info(mainimg.isEmpty());
        if(!mainimg.isEmpty()){
            log.info("들어온 대표이미지 이름");
            log.info(mainimg.getOriginalFilename());
        } else{
            log.info("등록할 대표이미지가 없습니다.");
        }
        if(multipartFile != null) {
            for (MultipartFile img : multipartFile) {
                if(!img.isEmpty()){
                    log.info("들어온 상세이미지");
                    log.info(img.getOriginalFilename());
                }
            }
        }else{
            log.info("등록할 상세이미지가 없습니다.");
        }
        if(delino != null && delino.length!=0){
            log.info(Arrays.toString(delino));
        }else{log.info("삭제할 번호가 없습니다.");}

        try {
            itemService.update(itemDTO, multipartFile, mainimg, delino);
        }catch (IOException e){
            log.info("수정 대상을 찾을 수 없습니다.");
            /*todo 리다이렉트 어트리뷰트 추가 예정*/
            return "redirect:/admin/item/read?id="+itemDTO.getId();
        }
        return null;
    }
}