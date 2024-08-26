package org.g9project4.publicData.tour.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.g9project4.global.CommonSearch;
import lombok.*;
import org.g9project4.global.RequestPage;
import org.g9project4.publicData.tour.constants.ContentType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPlaceSearch extends CommonSearch {
    /**
     * 필터 옵션
     *
     * contentType : 관광지 타입
     *
     *
     */
    private Double latitude;
    private Double longitude;
    private ContentType contentType;
    /**
     * 검색 옵션
     * TITLE : 여행지 이름
     * ADDRESS : 주소
     * TITLE_ADDRESS : 이름 + 주소
     * ALL:
     */

    private List<Long> seq; // 게시글 번호
    private Integer radius = 1000;

    // 추가된 정렬 기준
    private String sort; // 정렬 기준 (예: "contentId", "placePointValue")
    private String sortDirection; // 정렬 방향 (예: "asc" 또는 "desc")
}
