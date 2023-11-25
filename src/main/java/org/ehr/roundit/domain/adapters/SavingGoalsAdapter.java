package org.ehr.roundit.domain.adapters;

import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.SavingsGoal;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;

public interface SavingGoalsAdapter {
    SavingsGoal createSavingsGoal(String accessToken, String accountUid, String name, Currency currency) throws UnableToProvideDataException;
    void addMoney(String accessToken, String accountUid, SavingsGoal savingsGoal, CurrencyAndAmount currencyAndAmount) throws UnableToProvideDataException;
}
