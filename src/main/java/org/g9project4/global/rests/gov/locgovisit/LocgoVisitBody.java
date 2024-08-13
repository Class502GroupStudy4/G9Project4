package org.g9project4.global.rests.gov.locgovisit;

import lombok.Data;

@Data
public class LocgoVisitBody {
    private LocgoVisitItems items;
    private Integer numOfRows;
    private Integer pageNo;
    private Integer totalCount;
}
