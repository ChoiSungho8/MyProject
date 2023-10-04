package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.Member;
import org.zerock.shop.entity.Order;
import org.zerock.shop.entity.OrderItem;
import org.zerock.shop.repository.ItemRepository;
import org.zerock.shop.repository.MemberRepository;
import org.zerock.shop.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

// 주문 로직을 작성하기 위한 클래스
@Service
@Transactional // 성공 시 커밋, 실패 시 롤백
@RequiredArgsConstructor // final이나 @NonNull이 붙은 필드 생성자 자동 생성
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    public Long order(OrderDto orderDto, String email) {
        // 주문할 상품을 조회합니다.
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        // 현재 로그인한 회원의 이메일 정보를 이용해서 회원 정보를 조회합니다.
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        //  주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티를 생성합니다.
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        // 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성합니다.
        Order order = Order.createOrder(member, orderItemList);
        // 생성한 주문 엔티티를 저장합니다.
        orderRepository.save(order);

        return order.getId();
    }

}
