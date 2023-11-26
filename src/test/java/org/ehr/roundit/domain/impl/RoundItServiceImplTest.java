package org.ehr.roundit.domain.impl;

import org.ehr.roundit.domain.RoundItService;
import org.ehr.roundit.domain.adapters.*;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoundItServiceImplTest {
    @Mock
    private AccountsAdapter accountsAdapter;
    @Mock
    private SavingGoalsAdapter savingGoalsAdapter;
    @Mock
    private TransactionsAdapter transactionsAdapter;
    @Mock
    private RoundItCalculator roundItCalculator;
    @Mock
    private TimePeriodFactory timePeriodFactory;

    private RoundItService roundItService;

    @BeforeEach
    public void setup() {
        reset(accountsAdapter, savingGoalsAdapter, transactionsAdapter, roundItCalculator, timePeriodFactory);
        roundItService = new RoundItServiceImpl(accountsAdapter,
                transactionsAdapter, savingGoalsAdapter, timePeriodFactory, roundItCalculator);
    }

    @Test
    @DisplayName("Should create savings goal and add money to it, when account has sufficient funds")
    public void happyPath() throws UnableToProvideDataException {
        Currency testCurrency = Currency.GBP;
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);

        UserAccount primaryAccount = mock(UserAccount.class);
        when(primaryAccount.getCurrency()).thenReturn(testCurrency);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);
        when(accountsAdapter.getEffectiveBalance(testToken, testAccountUid)).thenReturn(
                CurrencyAndAmountMock.builder()
                        .currency(testCurrency)
                        .minorUnits(new BigInteger("100000"))
                        .build());

        TimePeriod mockPeriod = TimePeriod.builder()
                .maxTransactionTimestamp("maxTimestamp")
                .minTransactionTimestamp("minTimestamp")
                .build();
        when(timePeriodFactory.getTimePeriodForType(saverType)).thenReturn(mockPeriod);

        List<FeedItem> mockSettledPayments = List.of();
        CurrencyAndAmount testSavings = CurrencyAndAmountMock.builder()
                .currency(testCurrency)
                .minorUnits(new BigInteger("1000"))
                .build();
        when(transactionsAdapter.getSettledPaymentsBetween(testToken, testAccountUid, mockPeriod))
                .thenReturn(mockSettledPayments);
        when(roundItCalculator.calculateTotalRoundUp(primaryAccount, mockSettledPayments)).thenReturn(testSavings);

        SavingsGoal savingsGoal = mock(SavingsGoal.class);
        when(savingGoalsAdapter.createSavingsGoal(any(), any(), any(), any())).thenReturn(savingsGoal);

        BigInteger totalSavings = roundItService.roundItAndSave(portData);

        assertEquals("1000", totalSavings.toString());
        verify(savingGoalsAdapter, times(1))
                .createSavingsGoal(testToken, testAccountUid, "LAST_WEEK_maxTimestamp", testCurrency);
        verify(savingGoalsAdapter, times(1))
                .addMoney(testToken, testAccountUid, savingsGoal, testSavings);
    }

    @Test
    @DisplayName("Should not create savings goal/add money to it, when account has insufficient funds")
    public void unhappyPath() throws UnableToProvideDataException {
        Currency testCurrency = Currency.GBP;
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);
        UserAccount primaryAccount = mock(UserAccount.class);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);
        when(accountsAdapter.getEffectiveBalance(testToken, testAccountUid)).thenReturn(
                CurrencyAndAmountMock.builder()
                        .currency(testCurrency)
                        .minorUnits(new BigInteger("100"))
                        .build());

        TimePeriod mockPeriod = TimePeriod.builder()
                .maxTransactionTimestamp("maxTimestamp")
                .minTransactionTimestamp("minTimestamp")
                .build();
        when(timePeriodFactory.getTimePeriodForType(saverType)).thenReturn(mockPeriod);

        List<FeedItem> mockSettledPayments = List.of();
        CurrencyAndAmount testSavings = CurrencyAndAmountMock.builder()
                .currency(testCurrency)
                .minorUnits(new BigInteger("10000"))
                .build();
        when(transactionsAdapter.getSettledPaymentsBetween(testToken, testAccountUid, mockPeriod))
                .thenReturn(mockSettledPayments);
        when(roundItCalculator.calculateTotalRoundUp(primaryAccount, mockSettledPayments)).thenReturn(testSavings);

        BigInteger totalSavings = roundItService.roundItAndSave(portData);

        assertEquals("0", totalSavings.toString());
        verifyNoInteractions(savingGoalsAdapter);
    }

    @Test
    @DisplayName("Should throw exception when cannot get account")
    public void unhappyPathCannotGetAccount() throws UnableToProvideDataException {
        RoundItPortData portData = mock(RoundItPortData.class);

        doThrow(new UnableToProvideDataException("Kaboom!")).when(accountsAdapter)
                .getUserAccount(any(), any());

        assertThrows(UnableToProvideDataException.class, () -> roundItService.roundItAndSave(portData));
    }

    @Test
    @DisplayName("Should throw exception when cannot get time period")
    public void unhappyPathCannotGetTimePeriod() throws UnableToProvideDataException {
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);

        UserAccount primaryAccount = mock(UserAccount.class);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);
        doThrow(new UnableToProvideDataException("Kaboom!")).when(timePeriodFactory)
                .getTimePeriodForType(any());

        assertThrows(UnableToProvideDataException.class, () -> roundItService.roundItAndSave(portData));
    }

    @Test
    @DisplayName("Should throw exception when cannot get settled payments")
    public void unhappyPathCannotGetSettledPayments() throws UnableToProvideDataException {
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);

        UserAccount primaryAccount = mock(UserAccount.class);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);

        TimePeriod mockPeriod = TimePeriod.builder()
                .maxTransactionTimestamp("maxTimestamp")
                .minTransactionTimestamp("minTimestamp")
                .build();
        when(timePeriodFactory.getTimePeriodForType(saverType)).thenReturn(mockPeriod);

        doThrow(new UnableToProvideDataException("Kaboom!")).when(transactionsAdapter)
                .getSettledPaymentsBetween(any(), any(), any());

        assertThrows(UnableToProvideDataException.class, () -> roundItService.roundItAndSave(portData));
    }

    @Test
    @DisplayName("Should throw exception when cannot get account balance")
    public void unhappyPathCannotGetBalance() throws UnableToProvideDataException {
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);

        UserAccount primaryAccount = mock(UserAccount.class);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);

        TimePeriod mockPeriod = TimePeriod.builder()
                .maxTransactionTimestamp("maxTimestamp")
                .minTransactionTimestamp("minTimestamp")
                .build();
        when(timePeriodFactory.getTimePeriodForType(saverType)).thenReturn(mockPeriod);

        List<FeedItem> mockSettledPayments = List.of();
        when(transactionsAdapter.getSettledPaymentsBetween(testToken, testAccountUid, mockPeriod))
                .thenReturn(mockSettledPayments);
        doThrow(new UnableToProvideDataException("Kaboom!")).when(accountsAdapter).getEffectiveBalance(any(), any());

        assertThrows(UnableToProvideDataException.class, () -> roundItService.roundItAndSave(portData));
    }

    @Test
    @DisplayName("Should throw exception when cannot calculate round up")
    public void unhappyPathCannotCalculateRoundUp() throws UnableToProvideDataException {
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);

        UserAccount primaryAccount = mock(UserAccount.class);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);
        TimePeriod mockPeriod = TimePeriod.builder()
                .maxTransactionTimestamp("maxTimestamp")
                .minTransactionTimestamp("minTimestamp")
                .build();
        when(timePeriodFactory.getTimePeriodForType(saverType)).thenReturn(mockPeriod);

        List<FeedItem> mockSettledPayments = List.of();
        when(transactionsAdapter.getSettledPaymentsBetween(testToken, testAccountUid, mockPeriod))
                .thenReturn(mockSettledPayments);
        doThrow(new UnableToProvideDataException("Kaboom!")).when(roundItCalculator).calculateTotalRoundUp(any(), any());

        assertThrows(UnableToProvideDataException.class, () -> roundItService.roundItAndSave(portData));
    }

    @Test
    @DisplayName("Should throw exception when cannot create savings goal")
    public void unhappyPathCannotCreateSavingsGoal() throws UnableToProvideDataException {
        Currency testCurrency = Currency.GBP;
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);

        UserAccount primaryAccount = mock(UserAccount.class);
        when(primaryAccount.getCurrency()).thenReturn(testCurrency);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);
        when(accountsAdapter.getEffectiveBalance(testToken, testAccountUid)).thenReturn(
                CurrencyAndAmountMock.builder()
                        .currency(testCurrency)
                        .minorUnits(new BigInteger("100000"))
                        .build());

        TimePeriod mockPeriod = TimePeriod.builder()
                .maxTransactionTimestamp("maxTimestamp")
                .minTransactionTimestamp("minTimestamp")
                .build();
        when(timePeriodFactory.getTimePeriodForType(saverType)).thenReturn(mockPeriod);

        List<FeedItem> mockSettledPayments = List.of();
        CurrencyAndAmount testSavings = CurrencyAndAmountMock.builder()
                .currency(testCurrency)
                .minorUnits(new BigInteger("1000"))
                .build();
        when(transactionsAdapter.getSettledPaymentsBetween(testToken, testAccountUid, mockPeriod))
                .thenReturn(mockSettledPayments);
        when(roundItCalculator.calculateTotalRoundUp(primaryAccount, mockSettledPayments)).thenReturn(testSavings);

        doThrow(new UnableToProvideDataException("Kaboom!")).when(savingGoalsAdapter)
                .createSavingsGoal(any(), any(), any(), any());

        assertThrows(UnableToProvideDataException.class, () -> roundItService.roundItAndSave(portData));
    }

    @Test
    @DisplayName("Should throw exception when cannot add savings money")
    public void unhappyPathCannotAddSavingsMoney() throws UnableToProvideDataException {
        Currency testCurrency = Currency.GBP;
        String testToken = "token";
        String testAccountUid = "uid";
        String saverType = "LAST_WEEK";

        RoundItPortData portData = mock(RoundItPortData.class);
        when(portData.getAccessToken()).thenReturn(testToken);
        when(portData.getAccountUid()).thenReturn(testAccountUid);
        when(portData.getSaverType()).thenReturn(saverType);

        UserAccount primaryAccount = mock(UserAccount.class);
        when(primaryAccount.getCurrency()).thenReturn(testCurrency);

        when(accountsAdapter.getUserAccount(testToken, testAccountUid)).thenReturn(primaryAccount);
        when(accountsAdapter.getEffectiveBalance(testToken, testAccountUid)).thenReturn(
                CurrencyAndAmountMock.builder()
                        .currency(testCurrency)
                        .minorUnits(new BigInteger("100000"))
                        .build());

        TimePeriod mockPeriod = TimePeriod.builder()
                .maxTransactionTimestamp("maxTimestamp")
                .minTransactionTimestamp("minTimestamp")
                .build();
        when(timePeriodFactory.getTimePeriodForType(saverType)).thenReturn(mockPeriod);

        List<FeedItem> mockSettledPayments = List.of();
        CurrencyAndAmount testSavings = CurrencyAndAmountMock.builder()
                .currency(testCurrency)
                .minorUnits(new BigInteger("1000"))
                .build();
        when(transactionsAdapter.getSettledPaymentsBetween(testToken, testAccountUid, mockPeriod))
                .thenReturn(mockSettledPayments);
        when(roundItCalculator.calculateTotalRoundUp(primaryAccount, mockSettledPayments)).thenReturn(testSavings);

        SavingsGoal savingsGoal = mock(SavingsGoal.class);
        when(savingGoalsAdapter.createSavingsGoal(any(), any(), any(), any())).thenReturn(savingsGoal);
        doThrow(new UnableToProvideDataException("Kaboom!")).when(savingGoalsAdapter)
                .addMoney(any(), any(), any(), any());

        assertThrows(UnableToProvideDataException.class, () -> roundItService.roundItAndSave(portData));
    }
}
