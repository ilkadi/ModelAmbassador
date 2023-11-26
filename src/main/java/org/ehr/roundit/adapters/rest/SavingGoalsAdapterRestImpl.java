package org.ehr.roundit.adapters.rest;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.adapters.rest.entities.CreateOrUpdateSavingsGoalResponseV2;
import org.ehr.roundit.adapters.rest.entities.SavingsGoalRequestV2;
import org.ehr.roundit.adapters.rest.entities.SavingsGoalTransferResponseV2;
import org.ehr.roundit.adapters.rest.entities.TopUpRequestV2;
import org.ehr.roundit.domain.adapters.SavingGoalsAdapter;
import org.ehr.roundit.domain.entities.Currency;
import org.ehr.roundit.domain.entities.CurrencyAndAmount;
import org.ehr.roundit.domain.entities.SavingsGoal;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class SavingGoalsAdapterRestImpl implements SavingGoalsAdapter {

    private final WebClient webClient;

    @Override
    public SavingsGoal createSavingsGoal(String accessToken, String accountUid, String name, Currency currency) {
        return webClient.put()
                .uri("/api/v2/account/{accountUid}/savings-goals", accountUid)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.setBearerAuth(accessToken);
                })
                .bodyValue(SavingsGoalRequestV2.builder().name(name).currency(currency).build())
                .exchangeToMono(r -> r.statusCode().equals(HttpStatus.OK) ?
                        r.bodyToMono(CreateOrUpdateSavingsGoalResponseV2.class) :
                        r.createException().flatMap(x ->
                                Mono.error(new UnableToProvideDataException("Data retrieve failed", x.getCause()))))
                .block();
    }

    @Override
    public void addMoney(String accessToken, String accountUid, SavingsGoal savingsGoal,
                         CurrencyAndAmount currencyAndAmount) {
        UUID transferUid = UUID.randomUUID();
        webClient.put()
                .uri("/api/v2/account/{accountUid}/savings-goals/{savingsGoalUid}/add-money/{transferUid}",
                        accountUid, savingsGoal.getSavingsGoalUid(), transferUid)
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.setBearerAuth(accessToken);
                })
                .bodyValue(TopUpRequestV2.builder().amount(currencyAndAmount).build())
                .exchangeToMono(r -> r.statusCode().equals(HttpStatus.OK) ?
                        r.bodyToMono(SavingsGoalTransferResponseV2.class) :
                        r.createException().flatMap(x ->
                                Mono.error(new UnableToProvideDataException("Data retrieve failed", x.getCause()))))
                .block();
    }
}
