package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.shop.dto.ItemFormDto;
import org.zerock.shop.dto.ItemImgDto;
import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.dto.MainItemDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.ItemImg;
import org.zerock.shop.repository.ItemImgRepository;
import org.zerock.shop.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional // 성공하면 커밋, 실패하면 롤백
@RequiredArgsConstructor // final이나 @NonNull이 있는 필드의 생성자 자동 생성
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

    // 상품 데이터를 읽어오는 트랜잭션을 읽기 전용으로 설정
    // 이럴 경우 JPA가 더티체킹(변경감지)을 수행하지 않아서 성능을 향상 시킬 수 있다.
    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {

        // 해당 상품의 이미지를 조회합니다.
        // 등록순으로 가지고 오기 위해서 상품 이미지 아이디를 오름차순으로 가지고 옴
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        // 조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        // 상품의 아이디를 통해 상품 엔티티를 조회합니다.
        // 존재하지 않을 때는 EntityNotFoundException을 발생시킵니다.
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;

    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        // 상품 수정
        // 상품 등록 화면으로부터 전달 받은 상품 아이디를 이용하여 상품 엔티티를 조회
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
        // 상품 등록 화면으로부터 전달 받은 ItemFormDto를 통해 상품 엔티티를 업데이트
        item.updateItem(itemFormDto);

        // 상품 이미지 아이디 리스트를 조회
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            // 상품 이미지를 업데이트하기 위해서 updateItemImg() 메소드에 상품 이미지 아이디와,
            // 상품 이미지 파일 정보를 파라미터로 전달합니다.
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        return item.getId();

    }

    // 상품 조회 조건과 페이지 정보를 파라미터로 받아서 상품 데이터를 조회
    // 데이터의 수정이 일어나지 않으므로 최적화를 위해 @Transactional(readOnly = true) 설정
    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    // 메인 페이지 보여줄 상품 데이터를 조회하는 메소드
    // 데이터의 수정이 일어나지 않으므로 최적화를 위해 @Transactional(readOnly = true) 설정
    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

}
