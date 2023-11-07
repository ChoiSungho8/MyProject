package org.zerock.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;

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

    // 기간별 검색 조건을 고려
    private LocalDate from;
    private LocalDate to;
    // 완료 여부 고려
    private Boolean completed;

    // BoardRepository에서 String[]로 처리하기 때문에 type이라는 문자열을 배열로 반환해 주는 기능이 필요
    public String[] getTypes() {
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split("");
    }

    // 페이징 처리를 위해서 사용하는 Pageable 타입을 반환하는 기능도 있으면 편리하므로 메소드 구현
    public Pageable getPageable(String...props) {
        return PageRequest.of(this.page - 1, this.size, Sort.by(props).descending());
    }

    // 검색 조건과 페이징 조건 등을 문자열로 구성하는 getLink()를 추가
    private String link;

    public String getLink() {

        if (link == null) {
            StringBuilder builder = new StringBuilder();

            builder.append("page=" + this.page);

            builder.append("&size=" + this.size);

            if (type != null && type.length() > 0) {
                builder.append("&type=" + type);
            }

            if (keyword != null) {
                try {
                    builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                }
            }
            link = builder.toString();
        }
        return link;
    }

}
