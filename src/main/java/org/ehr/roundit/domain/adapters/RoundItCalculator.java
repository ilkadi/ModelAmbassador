package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.RoundItPeriod;
import org.ehr.roundit.domain.entities.UserAccount;

public interface RoundItCalculator {
    CurrencyAndAmount calculateTotalRoundUp(UserAccount account, RoundItPeriod roundItPeriod);
}
