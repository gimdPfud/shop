package com.example.shop.repository;

import com.example.shop.constant.ItemSellStatus;
import com.example.shop.entity.Item;
import com.example.shop.entity.QItem;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Log4j2
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;
    /*엔티티매니저 빈을 주입함...*/


    @Test
    public void selectSellerTest(){
        /*상품판매자 이메일*/
        String email = "tofu@a.a";
        List<Item> itemList = itemRepository.findByCreateBy(email);
        itemList.forEach(a->log.info(a));
    }

    //상품등록 기능을 만들기 위한 테스느
    //상품 등록을 잘 하는지 읽기 목록등을 테스트하기 위해서 더미값 만들기
    @Test
    public void insertTest(){
        //save() : 엔티티저장. insert into table (col, col ) values (값, 값)
        for (int i = 0; i < 100; i++) {
            Item item = new Item();
            item.setItemNm("테스트 상품"+i);
            item.setItemDetail(i+"테스트 상품 설명입니다.");
            item.setPrice(20000);
            item.setStockNumber(100);
            item.setItemSellStatus(ItemSellStatus.SELL);
            //만들어진 더미객체를
            //(엔티티로 만들어진 객체이므로 레포지토리를 통해 저장이 가능)
            log.info("설정한 아이템 엔티티 : ");
            log.info(item);
            Item result = itemRepository.save(item);
            log.info("결과 : "+result);
        }
    }

    @Test
    public void readTest(){
        //상품을 검색하자
        //select * from item where item_id = 1
        try {
            Item item =
                    itemRepository.findById(1L)
                            .orElseThrow(EntityNotFoundException::new);
            log.info("찾은 엔티티 : "+item);
        } catch (EntityNotFoundException e){
            log.info("아이템이 존재하지 않습니다.");
            e.printStackTrace();
            log.info("아이템이 존재하지 않습니다.");
        }
    }

    @Test
    public void findByItemNmTest(){
        //상품명으로 검색.
        //where item_nm = :itemNm || i.itemNm = :itemNm
        String itemNm = "테스트 상품";
        List<Item> itemList = itemRepository.findByItemNm(itemNm);
        log.info("아이템 리스트 출력 시작");
        itemList.forEach(item->log.info(item));
        /*이런 사소한 변수도 이름 맞춰주는 버릇을 들이자..*/
        log.info("아이템 리스트 출력 끝");
    }

    @Test
    public void findByPriceGreaterThanTest(){
        int price = 20000;/*쿼리문을 잘 보자*/
        List<Item> itemList =
                itemRepository.findByPriceGreaterThanEqual(price);
        itemList.forEach(item -> log.info(item));
    }

    @Test
    public void findByItemNmOrItemDetailTest(){
        //입력한 상품 명 또는 설명으로 찾기
        String keyword = "테스트 상품";
        List<Item> itemList =
                itemRepository.findByItemNmOrItemDetail(keyword,keyword);
        itemList.forEach(item -> log.info(item));
    }

    @Test
    public void findByItemNmContainingOrItemDetailContainingTest(){
        //입력한 상품 명 또는 설명으로 찾기
        String keyword = ".";
        List<Item> itemList =
                itemRepository.findByItemNmContainingOrItemDetailContaining(keyword,keyword);
        itemList.forEach(item -> log.info(item));
    }

    @Test
    public void selectItemNmDetailTest(){
        //입력한 상품 명 또는 설명으로 찾기
        String keyword = ".";
        List<Item> itemList =
                itemRepository.selectItemNmDetail(keyword);
        List<Item> itemList1 = itemRepository.selectItemNmDetail(keyword,keyword);
        itemList.forEach(item -> log.info(item));
        itemList1.forEach(item -> log.info(item));
    }

    @Test
    @DisplayName("수정테스트")
    public void updateTest(){
        //@Transactional 미사용
        try {
            Item item =
                    itemRepository.findById(1L)
                            .orElseThrow(EntityNotFoundException::new);
//        item.setId(1L);/*있는 번호*/
            item.setItemNm("상품명입력수정");
            item.setPrice(50000);
            log.info("저장하려는 엔티티 : " + item);
            //저장 하면 지정한 번호로 item엔티티의 값으로 저장된다...
//            Item result = itemRepository.save(item); 세이브 안하면 업데이트 쿼리를 안날림
//            log.info(result);
        }catch (EntityNotFoundException e){
            log.info("수정 대상을 찾을 수 없습니다.");
        }
    }

    @Test
    @Transactional
    @Rollback(value = false)
    public void updateTest2(){
        //트랜잭셔널 사용!!
        try {
            Item item =
                    itemRepository.findById(1L)
                            .orElseThrow(EntityNotFoundException::new);
            item.setItemNm("상품명입력수정");
            item.setPrice(50000); /*트랜잭셔널이 알아서 기본값과 바뀐점이 있는지 확인 후 update쿼리를 넣든말든..한다.*/
            log.info("저장하려는 엔티티 : " + item);

            itemRepository.delete(item);

        }catch (EntityNotFoundException e){
            log.info("수정 대상을 찾을 수 없습니다.");
        }
    }

    @Test
    public void queryDslTest(){
        JPQLQueryFactory queryFactory = new JPAQueryFactory(em);

        String keyword = "다";
        //Q도메인
        QItem qItem = QItem.item;

        String types = "n";
        String[] type = types.split("");
        log.info(Arrays.toString(type));

        JPQLQuery<Item> itemJPAQuery =
                queryFactory.selectFrom(qItem);

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        for (String str : type) {
            if(str.equals("n")){
                booleanBuilder.or(qItem.itemNm.like("%"+keyword+"%"));
            }else if(str.equals("d")){
                booleanBuilder.or(qItem.itemDetail.like("%"+keyword+"%"));
            }
        }
        itemJPAQuery.where(booleanBuilder);
        /*메소드체인으로 적었다는 말은??
        * 따로 써도 괜찮다는 뜻.
        * --> if조건을 걸어서 따로 적어도 괜찮다.
        * --> 쓸 필요가 있나? 싶지만 for문으로 반복 넣어도 된다는 말
        * --> 이것저것 할 수 있다! */
        List<Item> itemList = itemJPAQuery.fetch();

        log.info("총 수량 : "+itemJPAQuery.fetchCount());

        itemList.forEach(item -> log.info(item));
    }
}