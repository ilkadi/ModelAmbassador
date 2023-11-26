package org.ehr.roundit.adapters.rest.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ehr.roundit.domain.entities.AccountType;
import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.UserAccount;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountV2 implements UserAccount {
    private String accountUid;
    private AccountType accountType;
    private String defaultCategory;
    private Currency currency;
    private String createdAt;
    private String name;
}
