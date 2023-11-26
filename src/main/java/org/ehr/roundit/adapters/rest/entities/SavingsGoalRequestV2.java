package org.ehr.roundit.adapters.rest.entities;

import lombok.Builder;
import lombok.Data;
import org.ehr.roundit.domain.entities.Currency;

@Data
@Builder
public class SavingsGoalRequestV2 {
    private final String name;
    private final Currency currency;
}
