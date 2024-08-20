package org.g9project4.publicData.tour.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.g9project4.global.entities.BaseEntity;
import org.g9project4.publicData.tour.constants.ContentType;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourPlace extends BaseEntity {
    @Id
    private Long contentId;
    private Long contentTypeId;

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
    private Integer mapLevel;
    private Integer sigunguCode;


    @Transient
    public ContentType contentType;
}