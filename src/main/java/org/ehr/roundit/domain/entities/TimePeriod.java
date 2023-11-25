package org.ehr.roundit.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class TimePeriod {
    private final String minTransactionTimestamp;
    private final String maxTransactionTimestamp;
}
