package org.g9project4.global.rests.gov.metcoVisit;

import lombok.Data;

@Data
public class MetcoVisitBody {
    private MetcoVisitItems items;
    private Integer numOfRows;
    private Integer pageNo;
    private Integer totalCount;
}
