package org.ehr.roundit.domain.impl;

import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.domain.adapters.RoundItCalculator;
import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.FeedItem;
import org.ehr.roundit.domain.entities.UserAccount;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Component
public class RoundItCalculatorImpl implements RoundItCalculator {
    private static final String ONE_HUNDRED_STRING = "100";
    private static final BigInteger ONE_HUNDRED = new BigInteger(ONE_HUNDRED_STRING);

    @Override
    public CurrencyAndAmount calculateTotalRoundUp(UserAccount userAccount, List<FeedItem> settledPayments) {
        Currency currency = userAccount.getCurrency();
        boolean paymentHistoryExists = (settledPayments.size() > 0);

        if (paymentHistoryExists) {
            BigInteger projectedRoundItAmount = settledPayments.stream()
                    .map(item -> (new BigInteger(ONE_HUNDRED_STRING).subtract(
                            item.getAmount().getMinorUnits().remainder(ONE_HUNDRED))))
                    .reduce(BigInteger.ZERO, BigInteger::add);
            return CurrencyAndAmountDomainImpl.builder()
                    .currency(currency)
                    .minorUnits(projectedRoundItAmount)
                    .build();
        }  else {
            return CurrencyAndAmountDomainImpl.builder()
                    .currency(currency).minorUnits(BigInteger.ZERO)
                    .build();
        }
    }
}
