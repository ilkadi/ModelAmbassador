package org.ehr.roundit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RoundItApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoundItApplication.class, args);
    }
}
