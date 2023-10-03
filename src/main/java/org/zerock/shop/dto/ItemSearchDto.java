package org.zerock.shop.dto;

import lombok.Getter;
import lombok.Setter;
import org.zerock.shop.constant.ItemSellStatus;

@Getter @Setter
public class ItemSearchDto {
    // 상품 데이터 조회 시 상품 조회 조건을 가지고 있는 메소드

    // 현재 시간과 상품 등록일을 비교해서 상품 데이터를 조회합니다. 조회 시간 기준은 아래와 같다.
    // all : 상품 등록일 전체
    // 1d : 최근 하루 동안 등록된 상품
    // 1w : 최근 일주일 동안 등록된 상품
    // 1m : 최근 한 달 동안 등록된 상품
    // 6m : 최근 6개월 동안 등록된 상품
    private String searchDateType;

    // 상품의 판매상태를 기준으로 상품 데이터를 조회합니다.
    // SELL : 판매중, SOLD_OUT : 매진
    private ItemSellStatus searchSellStatus;

    // 상품을 조회할 때 어떤 유형으로 조회할 지 선택합니다.
    // itemNm : 상품명
    // createBy : 상품 등록자 아이디
    private String searchBy;

    // 조회할 검색어를 저장할 변수입니다.
    // searchBy가 itemNm일 경우 상품명을 기준으로 검색하고,
    // createBy일 경우 상품 등록자 아이디 기준으로 검색합니다.
    private String searchQuery = "";

}
