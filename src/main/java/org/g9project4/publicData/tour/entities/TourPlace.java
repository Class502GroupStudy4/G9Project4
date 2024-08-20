package org.g9project4.publicData.tour.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.g9project4.global.entities.BaseEntity;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table( )
public class TourPlace extends BaseEntity {
    @Id
    private Long contentId;
    private String contentTypeId;

    @Column(length = 30)
    private String category1;

    @Column(length = 30)
    private String category2;

    @Column(length = 30)
    private String category3;

    @Column(length = 100)
    private String title;

    @Column(length = 120)
    private String tel;

    @Column(length = 150)
    private String address;

    //@JoinColumn(name = "areaCode")
    private String areaCode;


    private boolean bookTour;
    private Double distance;

    private String firstImage;
    private String firstImage2;

    @Column(length = 30)
    private String cpyrhtDivCd;
    private Double latitude; // mapy
    private Double longitude; // mapx
    private Integer  mapLevel;


    private String sigunguCode2;
    private Integer placePointValue; //장소별 점수

    private Double type1D1; // 현지인 일별통계 현지인관광객구분명
    private Double type2D1; // 외지인 일별통계
    private Double type3D1; // 외국인 일별통계

    private Double type1W1; // 현지인 일별통계 현지인관광객구분명
    private Double type2W1; // 외지인 일별통계
    private Double type3W1; //외국인 일별통계

    private Double type1M1;  // 현지인 일별통계 현지인관광객구분명
    private Double type2M1; // 외지인 일별통계
    private Double type3M1; //외국인 일별통계

    private Double type1M3;  // 현지인 일별통계 현지인관광객구분명
    private Double type2M3; // 외지인 일별통계
    private Double type3M3; //외국인 일별통계

    private Double type1M6;  // 현지인 일별통계 현지인관광객구분명
    private Double type2M6; // 외지인 일별통계
    private Double type3M6; //외국인 일별통계

    private Double type1Y1;  // 현지인 일별통계 현지인관광객구분명
    private Double type2Y1; // 외지인 일별통계
    private Double type3Y1; //외국인 일별통계
}