package com.rewardtracker.swagger;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi rewardApi() {
        return GroupedOpenApi.builder()
                .group("reward-api")
                .packagesToScan("com.rewardtracker") // Adjusting here the package name as my project 
                .build();
    }
}
