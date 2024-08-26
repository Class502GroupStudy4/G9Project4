package org.g9project4.planner.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.g9project4.global.entities.BaseEntity;
import org.g9project4.member.entities.Member;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Planner extends BaseEntity {
    @Id
    @GeneratedValue
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Column(length = 100, nullable = false)
    private String title;//플래너 명

    @Column(nullable = false)
    private LocalDate sDate;//여행 시작일

    @Column(nullable = false)
    private LocalDate eDate;//여행 종료일

    @Lob
    @NotBlank
    private String itinerary;
}
