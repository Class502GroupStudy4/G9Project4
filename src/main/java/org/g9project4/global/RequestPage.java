package org.g9project4.global;

import lombok.Data;

@Data
public class RequestPage {
    private Integer page = 1;
    private Integer limit = 10;// 0 : 설정에 있는 1페이지 게시글 갯수, 1 이상 -> 지정한 갯수

    private String sopt; // 검색 옵션
    private String skey; // 검색 키워드
}
