package org.zerock.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;
import org.zerock.shop.constant.ItemSellStatus;
import org.zerock.shop.dto.ItemSearchDto;
import org.zerock.shop.dto.MainItemDto;
import org.zerock.shop.dto.QMainItemDto;
import org.zerock.shop.entity.Item;
import org.zerock.shop.entity.QItem;
import org.zerock.shop.entity.QItemImg;

import java.time.LocalDateTime;
import java.util.List;

// ItemRepositoryCustom을 상속
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    // 동적으로 쿼리를 생성하기 위해서 JPAQueryFactory 클래스를 사용합니다.
    private JPAQueryFactory queryFactory;

    // JPAQueryFactory의 생성자로 EntityManager 객체를 넣어줍니다.
    public ItemRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품 판매 상태 조건이 전체(null)일 경우는 null을 리턴합니다.
    // 결과값이 null이면 where절에서 해당 조건은 무시됩니다.
    // 상품 판매 상태 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회합니다.
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    // searchDateType의 값에 따라서 dateTime의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회
    // 예를 들어 searchDateType 값이 "1m"인 경우 dateTime의 시간을 한 달 전으로 세팅 후
    // 최근 한 달 동안 등록된 상품만 조회하도록 조건값을 반환합니다.
    private BooleanExpression regDtsAfter(String searchDateType) {
        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if(StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    // searchBy의 값에 따라서 상품명에 검색어를 포함하고 있는 상품 또는
    // 상품 생성자의 아이디에 검색어를 포함하고 있는 상품을 조회하도록 조건값을 반환
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {

        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;

    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        // QueryResults는 deprecated 되었다.
        // queryFactory를 이용해서 쿼리를 생성. 쿼리문의 형태와 문법이 비슷하다.
        // selectFrom(QItem.item) : 상품 데이터를 조회하기 위해서 QItem의 item을 지정
        // where 조건절 : BooleanExpression의 반환하는 조건문들을 넣어줍니다. ',' 단위로 넣어줄 경우 and 조건으로 인식
        // offset : 데이터를 가지고 올 시작 인덱스를 지정
        // limit : 한 번에 가지고 올 최대 개수를 지정
        // fetchReslut()는 deprecated 되었다.
        // fetch() : 조회 대상 리스트 반환
        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                        itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Wildcard.count = count(*)
        // fetchOne() : 조회 대상이 1건이면 해당 타입 반환, 1건 이상이면 에러 발생
        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                        itemSearchDto.getSearchQuery()))
                .fetchOne();

        // 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 반환합니다.
        return new PageImpl<>(content, pageable, total);

    }
    
    // 검색어가 null이 아니면 상품명에 해당 검색어가 포함되는 상품을 조회하는 조건을 반환
    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory
                .select(
                        // QMainItemDto의 생성자에 반환할 값들을 넣어줍니다.
                        // @QueryProjection을 사용하면 DTO로 바로 조회가 가능합니다.
                        // 엔티티 조회 후 DTO로 변환하는 과정을 줄일 수 있습니다.
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item) // itemImg와 item을 내부 조인합니다.
                .where(itemImg.repimgYn.eq("Y")) // 상품 이미지의 경우 대표 상품 이미지만 불러옵니다.
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Wildcard.count = count(*)
        // fetchOne() : 조회 대상이 1건이면 해당 타입 반환, 1건 이상이면 에러 발생
        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

}
