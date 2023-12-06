package org.ehr.ambassador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class ModelAmbassadorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModelAmbassadorApplication.class, args);
    }
}
