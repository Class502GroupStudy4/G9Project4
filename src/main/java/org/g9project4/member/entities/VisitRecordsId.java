package org.g9project4.member.entities;


import jakarta.persistence.Embeddable;
import lombok.*;
import org.g9project4.publicData.tour.entities.TourPlace;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VisitRecordsId implements Serializable {


    private Long seq;
    private Long contentId;
    private LocalDate visitDate;


    // 객체의 동등성 비교와 해시 코드 생성을 위해 사용
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitRecordsId that = (VisitRecordsId) o;
        return Objects.equals(seq, that.seq) &&
                Objects.equals(contentId, that.contentId) &&
                Objects.equals(visitDate, that.visitDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seq, contentId, visitDate);
    }
}
