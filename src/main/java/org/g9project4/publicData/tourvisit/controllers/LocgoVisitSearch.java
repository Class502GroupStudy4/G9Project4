package org.g9project4.publicData.tourvisit.controllers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.g9project4.publicData.tourvisit.entities.LocgoVisitId;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocgoVisitSearch {

    private int page=1;
    private int limit=10; // 0 : 설정에 있는 1페이지 게시글 갯수, 1 이상 -> 지정한 갯수
    /**
     * 검색 옵션
     *
     * contentType : 관광지 타입
     * radius : 거리
     * ALL :
     */

   // private LocgoVisitId locId;
    private Integer signguCode;
    private String touDivNm;
    private String signguNm;  //시군구명
    private String sopt; // 검색 옵션
    private String skey; // 검색 키워드

    private List<Long> seq; // 게시글 번호
    private Integer radius = 1000;
}
