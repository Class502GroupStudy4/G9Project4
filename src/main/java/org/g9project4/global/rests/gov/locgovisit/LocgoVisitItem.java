package org.g9project4.global.rests.gov.locgovisit;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocgoVisitItem {
    private Long signguCode;	//시군구코드

    private String signguNm;  //시군구명

    private String daywkDivCd;	//요일구분코드

    private String daywkDivNm;	// 요일구분명

    private Integer touDivCd;	 //관광객구분코드

    private String touDivNm;	 //관광객구분명

    private Double touNum;	 //관광객수

    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate baseYmd;	  //기준연월일

}
