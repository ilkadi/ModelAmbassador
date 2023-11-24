package org.ehr.roundit.domain.impl;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.ehr.roundit.domain.RoundItService;
import org.ehr.roundit.domain.adapters.AccountsAdapter;
import org.ehr.roundit.domain.adapters.RoundItCalculator;
import org.ehr.roundit.domain.adapters.SavingGoalsAdapter;
import org.ehr.roundit.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoundItServiceImplTest {
    @Mock
    private AccountsAdapter accountsAdapter;
    @Mock
    private SavingGoalsAdapter savingGoalsAdapter;
    @Mock
    private RoundItCalculator roundItCalculator;

    private RoundItService roundItService;

    @BeforeEach
    public void setup() {
        reset(accountsAdapter, savingGoalsAdapter, roundItCalculator);
        roundItService = new RoundItServiceImpl(accountsAdapter, savingGoalsAdapter, roundItCalculator);
    }

    @Test
    @DisplayName("Should create savings goal and add money to it, when Primary and Additional accounts" +
            "have sufficient funds and correct currency type and there are no comms issues")
    public void happyPath() {
        Currency testCurrency = Currency.GBP;
        String saverGoalName = "Happy Test Name";

        UserAccount primaryAccount = mock(UserAccount.class);
        when(primaryAccount.getAccountType()).thenReturn(AccountType.PRIMARY);
        when(primaryAccount.getCurrency()).thenReturn(testCurrency);

        UserAccount additionalAccount = mock(UserAccount.class);
        when(additionalAccount.getAccountType()).thenReturn(AccountType.ADDITIONAL);
        when(additionalAccount.getCurrency()).thenReturn(testCurrency);

        when(accountsAdapter.getUserAccounts()).thenReturn(List.of(primaryAccount, additionalAccount));
        when(accountsAdapter.getEffectiveBalance(primaryAccount)).thenReturn(
                        CurrencyAndAmountMock.builder()
                                .currency(testCurrency)
                                .minorUnits(new BigInteger("100000"))
                                .build());
        when(accountsAdapter.getEffectiveBalance(additionalAccount)).thenReturn(
                CurrencyAndAmountMock.builder()
                        .currency(testCurrency)
                        .minorUnits(new BigInteger("200000"))
                        .build());
        when(roundItCalculator.calculateTotalRoundUp(primaryAccount, RoundItPeriod.LAST_WEEK)).thenReturn(
                CurrencyAndAmountMock.builder()
                        .currency(testCurrency)
                        .minorUnits(new BigInteger("1000"))
                        .build());
        when(roundItCalculator.calculateTotalRoundUp(additionalAccount, RoundItPeriod.LAST_WEEK)).thenReturn(
                CurrencyAndAmountMock.builder()
                        .currency(testCurrency)
                        .minorUnits(new BigInteger("2000"))
                        .build());

        BigInteger totalSavings = roundItService.weeklySaverForCurrency(saverGoalName, testCurrency);

        assertEquals("3000", totalSavings.toString());
        verify(savingGoalsAdapter, times(1)).createSavingsGoal(saverGoalName, testCurrency);
        verify(savingGoalsAdapter, times(2)).addMoney(any(), any(), any());
    }

    @Data
    @Builder
    @Getter
    public static class CurrencyAndAmountMock implements CurrencyAndAmount {
        private Currency currency;
        private BigInteger minorUnits;
    }
}
