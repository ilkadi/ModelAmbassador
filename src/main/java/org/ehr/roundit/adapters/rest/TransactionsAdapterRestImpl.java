package org.ehr.roundit.adapters.rest;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.roundit.adapters.rest.entities.FeedItems;
import org.ehr.roundit.domain.adapters.TransactionsAdapter;
import org.ehr.roundit.domain.entities.Direction;
import org.ehr.roundit.domain.entities.FeedItem;
import org.ehr.roundit.domain.entities.TimePeriod;
import org.ehr.roundit.domain.entities.UnableToProvideDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionsAdapterRestImpl implements TransactionsAdapter {

    private final WebClient webClient;

    @Override
    public List<FeedItem> getSettledOutPaymentsBetween(String accessToken, String accountUid, TimePeriod timePeriod)
            throws UnableToProvideDataException {
        FeedItems items = webClient.get()
                .uri(builder -> builder
                        .path(String.format("/api/v2/feed/account/%s/settled-transactions-between", accountUid))
                        .queryParam("minTransactionTimestamp", timePeriod.getMinTransactionTimestamp())
                        .queryParam("maxTransactionTimestamp", timePeriod.getMaxTransactionTimestamp())
                        .build())
                .headers(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.setBearerAuth(accessToken);
                })
                .exchangeToMono(r -> r.statusCode().equals(HttpStatus.OK) ?
                        r.bodyToMono(FeedItems.class) :
                        r.createException().flatMap(x ->
                                Mono.error(new UnableToProvideDataException("Data retrieve failed", x.getCause()))))
                .block();

        if (Objects.isNull(items) || Objects.isNull(items.getFeedItems())) {
            throw new UnableToProvideDataException("Failed to load effective balance");
        }
        return Arrays.stream(items.getFeedItems())
                .filter(feed -> feed.getDirection().equals(Direction.OUT))
                .collect(Collectors.toList());
    }
}
