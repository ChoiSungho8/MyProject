package org.zerock.shop.exception;

// RuntimeException을 상속받아 사용자 정의 예외를 작성할 수 있다.
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message) {
        super(message);
    }

}
