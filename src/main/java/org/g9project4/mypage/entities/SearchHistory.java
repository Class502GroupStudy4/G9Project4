package org.g9project4.mypage.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.g9project4.member.entities.Member;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_history")
@Getter @Setter
@Data
public class SearchHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // 사용자를 참조하는 엔티티

    @Column(name = "search_term", nullable = false)
    private String searchTerm;

    @Column(name = "search_time", nullable = false)
    private LocalDateTime searchTime;
}
