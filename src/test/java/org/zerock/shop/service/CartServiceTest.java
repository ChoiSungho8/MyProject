package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.CartItemDto;
import org.zerock.shop.entity.CartItem;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Member;
import org.zerock.shop.repository.CartItemRepository;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    // 테스트를 위해서 장바구니에 담을 상품을 저장하는 메소드 생성
    public Item saveItem() {
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        return itemRepository.save(item);
    }

    // 테스트를 위해서 회원 정보를 저장하는 메소드 생성
    public Member saveMember() {
        Member member = new Member();
        member.setEmail("test@test.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("장바구니 담기 테스트")
    public void addCart() {
        // 테스트 상품 생성
        Item item = saveItem();
        // 테스트 회원 생성
        Member member = saveMember();

        CartItemDto cartItemDto = new CartItemDto();
        // 장바구니에 담을 상품과 수량을 cartItemDto 객체에 세팅
        cartItemDto.setCount(5);
        cartItemDto.setItemId(item.getId());

        // 상품을 장바구니에 담는 로직 호출 결과 생성된 장바구니 상품 아이디를 cartItemId 변수에 저장합니다.
        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());

        // 장바구니 상품 아이디를 이용하여 생성된 장바구니 상품 정보를 조회합니다.
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        // 상품 아이디와 장바구니에 저장된 상품 아이디가 같다면 테스트가 통과합니다.
        assertEquals(item.getId(), cartItem.getItem().getId());
        // 장바구니에 담았던 수량과 실제로 장바구니에 저장된 수량이 같다면 테스트가 통과합니다.
        assertEquals(cartItemDto.getCount(), cartItem.getCount());

    }

}
