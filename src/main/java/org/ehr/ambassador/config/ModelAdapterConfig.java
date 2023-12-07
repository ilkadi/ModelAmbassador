package org.ehr.ambassador.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ModelAdapterConfig {
    public static final String URL_PORT_TEMPLATE = "%s:%s";

    @Value("${model-ambassador.model-url}")
    private String modelUrl;

    @Value("${model-ambassador.model-port}")
    private String modelPort;

    @Bean
    public WebClient modelWebClient() {
        return WebClient.builder().baseUrl(
                        String.format(URL_PORT_TEMPLATE, modelUrl, modelPort))
                .build();
    }
}
