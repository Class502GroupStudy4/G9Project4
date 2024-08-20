package org.g9project4.publicData.tour.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceDetail {
    @Id
    private Long contentId;
    private String contentTypeId;

    private String title;

}
