package com.SAFE_Rescue.API_Perfiles.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient companiaWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8081/api/companias")
                .build();
    }

    @Bean
    public WebClient estadoWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082/api/estados")
                .build();
    }
}