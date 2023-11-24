package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.UserAccount;

import java.util.List;

public interface AccountsAdapter {
    List<UserAccount> getUserAccounts();
    CurrencyAndAmount getEffectiveBalance(UserAccount userAccount);
}
