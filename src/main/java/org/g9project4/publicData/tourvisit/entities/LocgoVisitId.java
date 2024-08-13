package org.g9project4.publicData.tourvisit.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LocgoVisitId implements Serializable {

    private Long signguCode; // 시군구코드

    private String touDivNm; // 관광객구분명





}
