package org.ehr.roundit.adapters.rest;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.adapters.rest.entities.AccountV2;
import org.ehr.roundit.adapters.rest.entities.Accounts;
import org.ehr.roundit.adapters.rest.entities.BalanceV2;
import org.ehr.roundit.domain.adapters.AccountsAdapter;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.ehr.roundit.domain.entities.UserAccount;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class AccountsAdapterRestImpl implements AccountsAdapter {

    private final WebClient webClient;

    @Override
    public UserAccount getUserAccount(String accessToken, String accountUid) throws UnableToProvideDataException {
        Accounts rawAccounts = webClient.get()
                .uri("/api/v2/accounts")
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.setBearerAuth(accessToken);
                })
                .exchangeToMono(r -> r.statusCode().equals(HttpStatus.OK) ?
                        r.bodyToMono(Accounts.class) :
                        r.createException().flatMap(x ->
                                Mono.error(new UnableToProvideDataException("Data retrieve failed", x.getCause()))))
                .block();

        Optional<AccountV2> loadedAccount = Arrays.stream(
                Objects.requireNonNull(Objects.requireNonNull(rawAccounts).getAccounts()))
                .filter(a -> a.getAccountUid().equals(accountUid))
                .findFirst();
        return loadedAccount.orElseThrow(() -> new UnableToProvideDataException("Failed to load matching account"));
    }

    // To check whether the account has enough money to make a payment, use the confirmation of funds endpoint instead.
    @Override
    public CurrencyAndAmount getEffectiveBalance(String accessToken, String accountUid) throws UnableToProvideDataException {
        BalanceV2 balance = webClient.get()
                .uri("/api/v2/accounts/{accountUid}/balance", accountUid)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.setBearerAuth(accessToken);
                })
                .exchangeToMono(r -> r.statusCode().equals(HttpStatus.OK) ?
                        r.bodyToMono(BalanceV2.class) :
                        r.createException().flatMap(x ->
                                Mono.error(new UnableToProvideDataException("Data retrieve failed", x.getCause()))))
                .block();

        if (Objects.isNull(balance) || Objects.isNull(balance.getEffectiveBalance())) {
            throw new UnableToProvideDataException("Failed to load effective balance");
        }
        return balance.getEffectiveBalance();
    }
}
