package org.zerock.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDto {
    // 페이징 관련 정보(page/size) 외에 검색의 종류(type)와 키워드(keyword)를 추가해서 지정

    @Builder.Default // 디폴트 값을 주고 싶을 때 선언
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type; // 검색의 종류 t, c, w, tc, tw, twc

    private String keyword;

}
