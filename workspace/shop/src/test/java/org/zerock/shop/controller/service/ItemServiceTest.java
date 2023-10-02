package org.zerock.shop.controller.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.repository.ItemImgRepository;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // 통합 테스트를 제공하는 기본적인 스프링 부트 테스트 어노테이션
@Transactional // 성공하면 커밋, 실패하면 롤백
// 프로젝트에서 사용되는 다른 소스보다 우선 순위가 높은 구성 소스를 정의할 수 있습니다.
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemServiceTest {

    @Autowired // 생성자 자동 생성
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultipartFiles() throws Exception {
        // MockMultipartFile 클래스를 이용하여 가짜 MultipartFile 리스트를 만들어서 반환해주는 메소드

        List<MultipartFile> multipartFileList = new ArrayList<>();

        for(int i=0; i<5; i++) {
            String path = "C:/shop/item/";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                    "image/jpg", new byte[]{1,2,3,4});
            multipartFileList.add(multipartFile);
        }

        return multipartFileList;

    }

    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception {
        // 상품 등록 화면에서 입력 받는 상품 데이터를 세팅해줍니다.
        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("테스트상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품 입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStockNumber(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);
        // 상품 데이터와 이미지 정보를 파라미터로 넘겨서 저장 후 저장된 상품의 아이디 값을 반환 값으로 리턴

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        // .orElseThrow(EntityNotFoundException::new) : 찾는게 없으면 에러 발생시킴
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        // assertEquals는 두 개의 값이 같은지 비교
        // 입력한 상품 데이터와 실제로 저장된 상품 데이터가 같은지 확인합니다.
        assertEquals(itemFormDto.getItemNm(), item.getItemNm());
        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
        assertEquals(itemFormDto.getItemDetail(), item.getItemDetail());
        assertEquals(itemFormDto.getPrice(), item.getPrice());
        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName());
        // 상품 이미지는 첫 번째 파일의 원본 이미지 파일 이름만 같은지 확인

    }

}
