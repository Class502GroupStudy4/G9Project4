package org.g9project4.global.rests.gov.locgovisit;

import lombok.Data;
import org.g9project4.global.rests.gov.api.ApiHeader;

@Data
public class LocgoVisitResponse {
    private ApiHeader header;
    private LocgoVisitBody body;
}
