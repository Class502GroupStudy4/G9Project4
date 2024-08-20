package org.g9project4.global.rests.gov.api;

import lombok.Data;

@Data
public class ApiBody2 {
    private ApiItems2 items;
    private Integer numOfRows;
    private Integer pageNo;
    private Integer totalCount;
}
