package org.g9project4.member.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.g9project4.publicData.tour.entities.TourPlace;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "visit_record")
@IdClass(VisitRecordsId.class)
public class VisitRecord implements Serializable {

    @EmbeddedId
    private VisitRecordsId id;

    @Id
    @ManyToOne
    @JoinColumn(name = "seq", referencedColumnName = "seq")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "contentId", referencedColumnName = "contentId")
    private TourPlace tourPlace;

    @Id
    @Column(name = "visitate", nullable = false)
    private LocalDate visitDate; // Date of the visit

    @Column(name = "visit_count", nullable = false)
    private int visitCount; // Number of visits on that date

    @ElementCollection
    @CollectionTable(name = "visit_keywords", joinColumns = {
            @JoinColumn(name = "seq", referencedColumnName = "seq"),
            @JoinColumn(name = "contentId", referencedColumnName = "contentId"),
            @JoinColumn(name = "visitDate", referencedColumnName = "visitDate")
    })
    @Column(name = "keyword")
    private List<String> keywords; // Keywords associated with the visit (e.g., search keywords)


}
