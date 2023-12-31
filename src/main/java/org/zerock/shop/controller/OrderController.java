package org.zerock.shop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.zerock.shop.dto.OrderDto;
import org.zerock.shop.dto.OrderHistDto;
import org.zerock.shop.service.OrderService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

// 주문 관련 요청들을 처리하는 클래스
// 상품 주문에서 웹 페이지의 새로 고침 없이 서버에 주문을 요청하기 위해서 비동기 방식 사용
@Controller
@RequiredArgsConstructor // final이나 @NonNull이 붙은 필드 생성자 자동 생성
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                              BindingResult bindingResult, Principal principal) {
        // 스프링에서 비동기 처리를 할 때 @RequestBody와 @ResponseBody 어노테이션 사용
        // @RequestBody : HTTP 요청의 본문 body에 담긴 내용을 자바 객체로 전달
        // 검증하려는 객체 앞에 @Valid 선언, 파라미터로 bindingResult 객체 추가
        // 검사 후 결과는 bindingResult에 담고 bindingResult.hasErrors()를 호출하여 에러가 있는지 확인
        // @ResponseBody : 자바 객체를 HTTP 요청의 body로 전달
        // Principal : 자바의 표준 시큐리티 기술로, 로그인이 된 상태라면 계정 정보를 담고 있습니다.

        // 주문 정보를 받는 orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사
        if(bindingResult.hasErrors()) {
            // StringBuilder는 변경 가능한 문자열을 만들어 주기 때문에, String을 합치는 작업 시 하나의 대안이 될 수 있다.
            StringBuilder sb = new StringBuilder();
            // FieldError : 필드에서 에러가 발생시, 에러가 발생한 필드와 출력할 메시지를 저장하기 위한 객체이다.
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                // 에러 메시지를 sb 넣으며 합친다.
                sb.append(fieldError.getDefaultMessage());
            }
            // 에러 정보를 ResponseEntity 객체에 담아서 반환합니다.
            // 상태코드 전달 받는 객체타입 ResponseEntity(객체 타입) 입니다.
            // 리턴할때 인자로 HttpStatus.BAD_REQUEST를 리턴하면 (http 상태 코드를 bad_request로 전달한다는 뜻이다)
            // 뷰로 들어가면 아무것도 안 뜨면서 개발자 도구의 네트워크 탭에 들어가면 에러코드로 400번 에러가 뜬다
            // 리턴할때 HttpStatus.BAD_REQUEST가 400에러를 전달 한다는 뜻
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 현재 로그인 유저의 정보를 얻기 위해서 @Controller 어노테이션이 선언된 클래스에서
        // 메소드 인자로 principal 객체를 넘겨 줄 경우 해당 객체에 직접 접근할 수 있습니다.
        // principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회 합니다.
        // principal.getName() : 로그인 아이디를 가져옴
        String email = principal.getName();
        Long orderId;

        try { // 화면으로부터 넘어오는 주문 정보와 회원의 이메일 정보를 이용하여 주문 로직을 호출
            orderId = orderService.order(orderDto, email);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 결과값으로 생성된 주문 번호와 요청이 성공했다는 HTTP 응답 상태 코드를 반환합니다.
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);

    }

    // 구매이력을 조회할 수 있도록 지금까지 구현한 로직을 호출하는 메소드
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {

        // 한 번에 가지고 올 주문의 개수는 4개로 설정하겠습니다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        // 현재 로그인한 회원은 이메일과 페이징 객체를 파라미터로 전달하여 화면에 전달한 주문 목록 데이터를 리턴 값으로 받습니다.
        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";

    }

    // 주문번호(orderId)를 받아서 주문 취소 로직을 호출하는 메소드. 비동기 요청
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {

        // 자바스크립트에서 취소할 주문 번호는 조작이 가능하므로 다른 사람의 주문을 취소하지 못하도록 주문 취소 권한 검사를 합니다.
        if (!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        // 주문 취소 로직을 호출합니다.
        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);

    }

}
