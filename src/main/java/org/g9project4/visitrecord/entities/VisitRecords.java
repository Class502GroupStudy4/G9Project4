package org.g9project4.visitrecord.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.g9project4.global.entities.BaseEntity;
import org.g9project4.global.entities.BaseMemberEntity;
import org.g9project4.member.entities.Member;
import org.g9project4.visitrecord.constants.VisitType;

import java.io.Serializable;
import java.time.LocalDate;

//@Getter
//@Setter
//@NoArgsConstructor
//@Entity
//@Table(name = "visit_record")
//@IdClass(VisitRecordsId.class)
//public class VisitRecords extends BaseEntity {
//
//    @Id
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Member member;
//
//    private int visitCount; // Number of visits on that date
//
//    @Id
//    @Enumerated(EnumType.STRING)
//    @Column(length = 20)
//    private VisitType visitType;
//}
