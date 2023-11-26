package org.ehr.roundit.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value("${round-it.starling-api-url}")
    private String starlingApiUrl;

    @Bean
    public WebClient starlingWebClient() {
        return WebClient.builder().baseUrl(starlingApiUrl).build();
    }
}
