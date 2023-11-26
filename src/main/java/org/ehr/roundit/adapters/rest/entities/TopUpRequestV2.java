package org.ehr.roundit.adapters.rest.entities;

import lombok.Builder;
import lombok.Data;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;

@Data
@Builder
public class TopUpRequestV2 {
    private CurrencyAndAmount amount;
}
