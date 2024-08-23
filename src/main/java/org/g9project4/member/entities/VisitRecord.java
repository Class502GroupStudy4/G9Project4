package org.g9project4.member.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "visit_record")
public class VisitRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seq", nullable = false)
    private Long seq; // ID of the member who made the visit

    @Column(name = "content_id", nullable = false)
    private String contentId; // ID of the TourPlace (could be an identifier)

    @Column(name = "visit_date", nullable = false)
    private LocalDate visitDate; // Date of the visit

    @Column(name = "visit_count", nullable = false)
    private int visitCount; // Number of visits on that date

    @ElementCollection
    @CollectionTable(name = "visit_keywords", joinColumns = @JoinColumn(name = "visit_record_id"))
    @Column(name = "keyword")
    private List<String> keywords; // Keywords associated with the visit (e.g., search keywords)

    // Constructors, getters, and setters can be generated by Lombok
}
