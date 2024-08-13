package org.g9project4.global.rests.gov.metcoVisit;

import lombok.Data;
import org.g9project4.global.rests.gov.api.ApiHeader;

@Data
public class MetcoVisitResponse {
    private ApiHeader header;
    private MetcoVisitBody body;
}
