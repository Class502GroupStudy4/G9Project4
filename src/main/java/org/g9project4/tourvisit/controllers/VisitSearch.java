package org.g9project4.tourvisit.controllers;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.netflix.discovery.converters.Auto;
import lombok.*;
import org.g9project4.global.RequestPage;
import org.g9project4.publicData.tour.constants.ContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitSearch extends  RequestPage {


    private String contentTypeId;

   private String sigunguCode;
    private Long contentId;
    private ContentType contentType;


    private List<Long> seq; // 게시글 번호
    private Integer radius = 1000;


}
