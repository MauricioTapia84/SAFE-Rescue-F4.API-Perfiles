package com.SAFE_Rescue.API_Perfiles.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
public class WebClienteConfig {

    private final WebClient webClient;
    private final String companiaServiceUrl;
    private final String estadoServiceUrl;
    private final String fotoServiceUrl;

    public WebClienteConfig(@Value("${compania.service.url}") String companiaServiceUrl,
                            @Value("${estado.service.url}") String estadoServiceUrl,
                            @Value("${foto.service.url}") String fotoServiceUrl) {
        this.webClient = WebClient.builder().build();
        this.companiaServiceUrl = companiaServiceUrl;
        this.estadoServiceUrl = estadoServiceUrl;
        this.fotoServiceUrl = fotoServiceUrl;
    }

    public Map<String, Object> getCompaniaById(Long id) {
        return this.webClient.get()
                .uri(this.companiaServiceUrl + "/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Compania no encontrada (ID: " + id + ")")))
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, Object> getEstadoById(Long id) {
        return this.webClient.get()
                .uri(this.estadoServiceUrl + "/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Estado no encontrado (ID: " + id + ")")))
                .bodyToMono(Map.class)
                .block();
    }

    public String getFotoUrlById(Long id) {
        return this.webClient.get()
                .uri(this.fotoServiceUrl + "/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException("Foto no encontrada (ID: " + id + ")")))
                .bodyToMono(String.class)
                .block();
    }
}