package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.ehr.roundit.domain.entities.UserAccount;

public interface AccountsAdapter {
    UserAccount getUserAccount(String accessToken, String accountUid) throws UnableToProvideDataException;
    CurrencyAndAmount getEffectiveBalance(String accessToken, String accountUid) throws UnableToProvideDataException;
}
