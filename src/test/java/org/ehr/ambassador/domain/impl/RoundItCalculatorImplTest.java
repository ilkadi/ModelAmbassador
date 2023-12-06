package org.ehr.ambassador.domain.impl;

import org.ehr.ambassador.domain.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RoundItCalculatorImplTest {
    private RoundItCalculatorImpl roundItCalculator;

    @BeforeEach
    public void setup() {
        roundItCalculator = new RoundItCalculatorImpl();
    }

    @Test
    @DisplayName("Should return correctly rounded amount")
    public void happyPath() {
        Currency testCurrency = Currency.GBP;
        UserAccount primaryAccount = mock(UserAccount.class);
        when(primaryAccount.getCurrency()).thenReturn(testCurrency);

        List<FeedItem> settledPayments = List.of(
                FeedItemMock.builder().amount(
                                CurrencyAndAmountMock.builder().minorUnits(new BigInteger("435")).build())
                        .build(),
                FeedItemMock.builder().amount(
                                CurrencyAndAmountMock.builder().minorUnits(new BigInteger("520")).build())
                        .build(),
                FeedItemMock.builder().amount(
                                CurrencyAndAmountMock.builder().minorUnits(new BigInteger("087")).build())
                        .build());

        CurrencyAndAmount projectedSavings = roundItCalculator.calculateTotalRoundUp(primaryAccount, settledPayments);

        assertEquals(testCurrency, projectedSavings.getCurrency());
        assertEquals("158", projectedSavings.getMinorUnits().toString());
    }

    @Test
    @DisplayName("Should return zero with accounts currency if feed is empty")
    public void unhappyPath() {
        Currency testCurrency = Currency.GBP;
        UserAccount primaryAccount = mock(UserAccount.class);
        when(primaryAccount.getCurrency()).thenReturn(testCurrency);

        List<FeedItem> settledPayments = List.of();
        CurrencyAndAmount projectedSavings = roundItCalculator.calculateTotalRoundUp(primaryAccount, settledPayments);

        assertEquals(testCurrency, projectedSavings.getCurrency());
        assertEquals("0", projectedSavings.getMinorUnits().toString());
    }
}
