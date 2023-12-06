package org.ehr.ambassador.ports.rest.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.ehr.ambassador.domain.entities.RoundItPortData;

@Data
@Builder
@Getter
public class RoundItPortDataImpl implements RoundItPortData {
    private final String accountUid;
    private final String accessToken;
    private final String saverType;
}
