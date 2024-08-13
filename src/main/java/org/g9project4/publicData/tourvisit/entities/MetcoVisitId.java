package org.g9project4.publicData.tourvisit.entities;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class MetcoVisitId {

    private Long areaCode; //

    @JsonFormat(pattern = "yyyyMMdd")
    private LocalDate baseYmd;	  //기준연월일
}
