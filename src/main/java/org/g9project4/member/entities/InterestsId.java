package org.g9project4.member.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.g9project4.member.constants.Interest;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class InterestsId implements Serializable {

    private Member member;
    private Interest interest;
}
