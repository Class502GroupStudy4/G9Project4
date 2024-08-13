package org.g9project4.publicData.tourvisit.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(MetcoVisitId.class)
public class MetcoVisit {

    //  광역 지자체 지역방문자수 집계 데이터 정보 조회
    @Id
    private Long areaCode;

    @Id
    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate baseYmd;      //기준연월일

    private String touDivNm;     //관광객구분명


    private String areaNm;  //


    private String daywkDivCd;    //요일구분코드

    private String daywkDivNm;    // 요일구분명

    private Integer touDivCd;     //관광객구분코드

    private Double touNum;     //관광객수

}