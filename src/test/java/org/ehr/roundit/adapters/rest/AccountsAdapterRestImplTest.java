package org.ehr.roundit.adapters.rest;

import org.ehr.roundit.adapters.rest.entities.AccountV2;
import org.ehr.roundit.adapters.rest.entities.BalanceV2;
import org.ehr.roundit.adapters.rest.entities.CurrencyAndAmountImpl;
import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.ehr.roundit.domain.entities.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

import static org.ehr.roundit.domain.entities.AccountType.ADDITIONAL;
import static org.ehr.roundit.domain.entities.AccountType.PRIMARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @noinspection rawtypes, unchecked
 */
@ExtendWith(MockitoExtension.class)
public class AccountsAdapterRestImplTest {
    // TODO use mock web server instead, cover more API response cases

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;

    private AccountsAdapterRestImpl accountsAdapterRest;

    @BeforeEach
    public void setup() {
        reset(webClient);
        accountsAdapterRest = new AccountsAdapterRestImpl(webClient);
    }

    @Test
    @DisplayName("Should return user account")
    public void getAccountHappyPath() throws UnableToProvideDataException {
        String primaryAccountUid = "1";
        AccountV2 expectedAccount = new AccountV2(primaryAccountUid, PRIMARY, "", Currency.GBP, "", "1");
        AccountV2[] accounts = {
                expectedAccount,
                new AccountV2("2", ADDITIONAL, "", Currency.USD, "", "2")
        };

        when(webClient.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("/api/v2/accounts"))
               .thenReturn(requestHeadersMock);
        when(requestHeadersMock.headers(any())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.exchangeToMono(any())).thenReturn(Mono.just(accounts));

        UserAccount actualAccount = accountsAdapterRest.getUserAccount("token", primaryAccountUid);
        assertEquals(expectedAccount, actualAccount);
    }

    @Test
    @DisplayName("Should throw exception when fails to find correct account")
    public void getAccountUnhappyPath() {
        String primaryAccountUid = "1";
        AccountV2[] accounts = {
                new AccountV2("2", ADDITIONAL, "", Currency.USD, "", "2")
        };

        when(webClient.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock
                .uri("/api/v2/accounts"))
                .thenReturn(requestHeadersMock);
        when(requestHeadersMock.headers(any())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.exchangeToMono(any())).thenReturn(Mono.just(accounts));

        assertThrows(UnableToProvideDataException.class,
                () -> accountsAdapterRest.getUserAccount("token", primaryAccountUid));
    }

    @Test
    @DisplayName("Should return effective balance")
    public void getBalanceHappyPath() throws UnableToProvideDataException {
        String primaryAccountUid = "1";
        BalanceV2 expectedBalance = Mockito.mock(BalanceV2.class);
        CurrencyAndAmountImpl expectedEffectiveBalance = new CurrencyAndAmountImpl(Currency.GBP, new BigInteger("100"));
        when(expectedBalance.getEffectiveBalance()).thenReturn(expectedEffectiveBalance);

        when(webClient.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("/api/v2/accounts/{accountUid}/balance", primaryAccountUid))
                .thenReturn(requestHeadersMock);
        when(requestHeadersMock.headers(any())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.exchangeToMono(any())).thenReturn(Mono.just(expectedBalance));

        CurrencyAndAmount actualAmount = accountsAdapterRest.getEffectiveBalance("token", primaryAccountUid);
        assertEquals(expectedEffectiveBalance, actualAmount);
    }

    @Test
    @DisplayName("Should throw exception when fails to load effective balance")
    public void getBalanceUnhappyPath() {
        String primaryAccountUid = "1";
        BalanceV2 expectedBalance = Mockito.mock(BalanceV2.class);

        when(webClient.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("/api/v2/accounts/{accountUid}/balance", primaryAccountUid))
                .thenReturn(requestHeadersMock);
        when(requestHeadersMock.headers(any())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.exchangeToMono(any())).thenReturn(Mono.just(expectedBalance));

        assertThrows(UnableToProvideDataException.class,
                () -> accountsAdapterRest.getEffectiveBalance("token", primaryAccountUid));
    }
}
