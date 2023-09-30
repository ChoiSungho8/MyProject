package org.zerock.shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.repository.ItemImgRepository;
import org.zerock.shop.repository.ItemRepository;

import java.util.List;

@Service
@Transactional // 성공하면 커밋, 실패하면 롤백
@RequiredArgsConstructor // final이나 @NotNull이 있는 필드의 생성자 자동 생성
public class ItemService {
    // 상품을 등록하는 ItemService 클래스
    
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {
        
        // 상품 등록
        // 상품 등록 폼으로부터 입력 받은 데이터를 이용하여 item 객체를 생성
        Item item = itemFormDto.createItem();
        itemRepository.save(item); // 상품 데이터를 저장
        
        // 이미지 등록
        for (int i=0; i<itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            if (i == 0) { // 첫 번째 이미지일 경우 대표 상품 이미지 여부 값을 "Y"로 세팅
                // 나머지 상품 이미지는 "N"으로 설정
                itemImg.setRepimgYn("Y");
            } else {
                itemImg.setRepimgYn("N");
            }
            
            // 상품의 이미지 정보를 저장
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));

        }

        return item.getId();
        
    }
    
}
