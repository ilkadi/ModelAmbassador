package org.ehr.ambassador.adapters.rest;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.ambassador.domain.adapters.ModelAdapter;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModelAdapterRestImpl implements ModelAdapter {
    @Value("${model-ambassador.model-timeout-ms}")
    private int modelTimeout;
    private final WebClient webClient;

    @Override
    public Map<String, Object> processWithModel(Map<String, Object> modelData) throws ProcessingFailedException {
        try {
            return webClient.post()
                    .uri("/predict")
                    .headers(httpHeaders -> {
                        httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    })
                    .bodyValue(modelData)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(r -> r.statusCode().equals(HttpStatus.OK) ?
                            r.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                            }) :
                            r.createException().flatMap(x ->
                                    Mono.error(new ProcessingFailedException("Failed model response: " + r.statusCode(), x.getCause()))))
                    .block(Duration.of(modelTimeout, ChronoUnit.MILLIS));
        } catch (Throwable t) {
            throw new ProcessingFailedException("Failed to retrieve model output ", t);
        }
    }
}
