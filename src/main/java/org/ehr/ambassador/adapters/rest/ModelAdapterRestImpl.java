package org.ehr.ambassador.adapters.rest;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ehr.ambassador.domain.adapters.ModelAdapter;
import org.ehr.ambassador.domain.entities.ProcessingFailedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
public class ModelAdapterRestImpl implements ModelAdapter {

    private final WebClient webClient;

    @Override
    public Map<String, Object> processWithModel(Map<String, Object> modelData) throws ProcessingFailedException {
        Map<String, Object> modelOutputData = webClient.post()
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
                                Mono.error(new ProcessingFailedException("Model processing failed", x.getCause()))))
                .block();

        if (Objects.isNull(modelOutputData)) {
            throw new ProcessingFailedException("Failed to load model output");
        }
        return modelOutputData;
    }
}
