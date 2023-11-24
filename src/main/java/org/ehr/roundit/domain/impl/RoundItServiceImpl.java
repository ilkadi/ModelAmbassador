package org.ehr.roundit.domain.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.domain.RoundItService;
import org.ehr.roundit.domain.adapters.AccountsAdapter;
import org.ehr.roundit.domain.adapters.RoundItCalculator;
import org.ehr.roundit.domain.adapters.SavingGoalsAdapter;
import org.ehr.roundit.domain.entities.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoundItServiceImpl implements RoundItService {
    private static final Set<AccountType> consideredAccountTypes = Set.of(AccountType.PRIMARY, AccountType.ADDITIONAL);

    private final AccountsAdapter accountsAdapter;
    private final SavingGoalsAdapter savingGoalsAdapter;
    private final RoundItCalculator roundItCalculator;

    @Override
    public BigInteger weeklySaverForCurrency(String saverName, Currency currency) {
        List<UserAccount> currencyAccounts = accountsAdapter.getUserAccounts().stream()
                .filter(a -> currency.equals(a.getCurrency()))
                .filter(a -> consideredAccountTypes.contains(a.getAccountType())).toList();
        SavingsGoal weeklySaverGoal = savingGoalsAdapter.createSavingsGoal(saverName, currency);
        BigInteger totalSavings = BigInteger.ZERO;

        for (UserAccount account : currencyAccounts) {
            CurrencyAndAmount accountBalance = accountsAdapter.getEffectiveBalance(account);
            CurrencyAndAmount calculatedSaving = roundItCalculator.calculateTotalRoundUp(account, RoundItPeriod.LAST_WEEK);

            BigInteger accountBalanceIfSavingsApplied = accountBalance.getMinorUnits().subtract(calculatedSaving.getMinorUnits());
            if (accountBalanceIfSavingsApplied.compareTo(BigInteger.ZERO) >= 0) {
                savingGoalsAdapter.addMoney(account, weeklySaverGoal, calculatedSaving);
                totalSavings = totalSavings.add(calculatedSaving.getMinorUnits());
            } else {
                log.warn("Skipping weekly saver not to drive account into the negative space");
            }
        }
        return totalSavings;
    }
}
