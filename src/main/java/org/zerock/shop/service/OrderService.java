package org.zerock.shop.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.dto.OrderHistDto;
import org.zerock.shop.dto.OrderItemDto;
import org.zerock.shop.entity.*;
import org.zerock.shop.repository.ItemImgRepository;
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
    private final ItemImgRepository itemImgRepository;

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

    // 주문 목록을 조회하는 로직
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        // 유저의 아이디와 페이징 조건을 이용하여 주문 목록을 조회합니다.
        List<Order> orders = orderRepository.findOrders(email, pageable);
        // 유저의 주문 총 개수를 구합니다.
        Long totalCount = orderRepository.countOrder(email);
        // 주문 정보 객체 리스트로 생성
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        // 주문 리스트를 순회하면서 구매 이력 페이지에 전달할 DTO를 생성합니다.
        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                // 주문한 상품의 대표 이미지를 조회합니다.
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);

        }

        // 페이지 구현 객체를 생성하여 반환합니다.
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);

    }

    // 주문을 취소하는 로직을 구현
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        // 현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사를 합니다.
        // 같을 때는 true를 반환하고 같지 않을 경우는 false를 반환합니다.
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {
            return false;
        }

        return true;

    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        // 주문 취소 상태로 변경하면 변경 감지 기능에 의해서 트랜잭션이 끝날 때 update 쿼리가 실행됩니다.
        order.cancelOrder();
    }

}
