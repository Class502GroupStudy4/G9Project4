package org.g9project4.publicData.tourvisit.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
@IdClass(LocgoVisitId.class)   // 복합 키를 사용하는 경우
public class LocgoVisit {

    @Id
    private Long signguCode;

    @Id
    private String touDivNm;	 //관광객구분명

    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate baseYmd;	  //기준연월일





    private String signguNm;  //


    private String daywkDivCd;	//요일구분코드

    private String daywkDivNm;	// 요일구분명

    private Integer touDivCd;	 //관광객구분코드

    private Double touNum;	 //관광객수

}
