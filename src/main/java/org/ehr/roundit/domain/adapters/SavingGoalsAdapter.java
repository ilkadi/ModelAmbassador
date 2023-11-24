package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.SavingsGoal;
import org.ehr.roundit.domain.entities.UserAccount;

public interface SavingGoalsAdapter {
    SavingsGoal createSavingsGoal(String name, Currency currency);
    void addMoney(UserAccount userAccount, SavingsGoal savingsGoal, CurrencyAndAmount currencyAndAmount);
}
