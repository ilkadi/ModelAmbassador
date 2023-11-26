package org.ehr.roundit.domain.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.domain.RoundItService;
import org.ehr.roundit.domain.adapters.*;
import org.ehr.roundit.domain.entities.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoundItServiceImpl implements RoundItService {
    private final AccountsAdapter accountsAdapter;
    private final TransactionsAdapter transactionsAdapter;
    private final SavingGoalsAdapter savingGoalsAdapter;

    private final TimePeriodFactory timePeriodFactory;
    private final RoundItCalculator roundItCalculator;

    @Override
    public BigInteger roundItAndSave(RoundItPortData roundItPortData) throws UnableToProvideDataException {
        final String accessToken = roundItPortData.getAccessToken();
        final String accountUid = roundItPortData.getAccountUid();
        final String saverType = roundItPortData.getSaverType();

        UserAccount userAccount = accountsAdapter.getUserAccount(accessToken, accountUid);
        TimePeriod timePeriod = timePeriodFactory.getTimePeriodForType(saverType);
        List<FeedItem> settledPayments = transactionsAdapter
                .getSettledPaymentsBetween(accessToken, accountUid, timePeriod);

        CurrencyAndAmount accountBalance = accountsAdapter.getEffectiveBalance(accessToken, accountUid);
        CurrencyAndAmount projectedSavings = roundItCalculator.calculateTotalRoundUp(userAccount, settledPayments);
        boolean accountBalanceSufficient = accountBalance.getMinorUnits()
                .compareTo(projectedSavings.getMinorUnits()) >= 0;

        if (accountBalanceSufficient) {
            String saverName = roundItPortData.getSaverType() + "_" + timePeriod.getMaxTransactionTimestamp();
            Currency saverCurrency = userAccount.getCurrency();
            SavingsGoal savingsGoal = savingGoalsAdapter
                    .createSavingsGoal(accessToken, accountUid, saverName, saverCurrency);
            savingGoalsAdapter.addMoney(accessToken, accountUid, savingsGoal, projectedSavings);
            log.info("Account {}, saverType {}: saver {} successfully created with {} savings",
                    accountUid, saverType, savingsGoal.getSavingsGoalUid(), projectedSavings.getMinorUnits());
            return projectedSavings.getMinorUnits();
        } else {
            log.warn("Account {}, saverType {}: Skipping weekly saver not to drive account into the negative space",
                    accountUid, saverType);
            return BigInteger.ZERO;
        }
    }
}
