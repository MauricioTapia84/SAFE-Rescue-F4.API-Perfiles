package com.SAFE_Rescue.API_Perfiles.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.util.Map;

@Configuration
public class WebClienteConfig {

    private final WebClient webClient;
    private final String companiaServiceUrl;
    private final String estadoServiceUrl;
    private final String fotoServiceUrl;

    public WebClienteConfig(@Value("${compania.service.url}") String companiaServiceUrl,
                            @Value("${estado.service.url}") String estadoServiceUrl,
                            @Value("${foto.service.url}") String fotoServiceUrl,
                            WebClient.Builder webClientBuilder) { // Inyectamos el builder de Spring
        // Construimos una sola instancia de WebClient para todas las llamadas
        // Esto permite usar la configuración por defecto de Spring
        this.webClient = webClientBuilder.build();
        this.companiaServiceUrl = companiaServiceUrl;
        this.estadoServiceUrl = estadoServiceUrl;
        this.fotoServiceUrl = fotoServiceUrl;
    }

    // Método para obtener una compañía
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

    // Método para obtener un estado
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

    // Método para obtener la URL de una foto (revisado para ser más robusto)
    public String getFotoUrlById(Long id) {
        // Asumiendo que la API de fotos devuelve un objeto con un campo 'url'
        try {
            Map<String, Object> fotoData = this.webClient.get()
                    .uri(this.fotoServiceUrl + "/{id}", id)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError(),
                            response -> response.bodyToMono(String.class)
                                    .map(body -> new RuntimeException("Foto no encontrada (ID: " + id + ")")))
                    .bodyToMono(Map.class)
                    .block();

            // Retorna la URL si existe
            if (fotoData != null && fotoData.containsKey("url")) {
                return (String) fotoData.get("url");
            }
            throw new RuntimeException("URL de foto no encontrada en la respuesta de la API.");

        } catch (RuntimeException e) {
            // Manejar errores de la llamada HTTP
            throw new RuntimeException("Error al obtener la foto de la API externa: " + e.getMessage());
        }
    }

    // Método para subir una foto (corregido)
    public String uploadFoto(MultipartFile archivo) {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío.");
        }

        try {
            // Convierte el MultipartFile a ByteArrayResource para WebClient
            ByteArrayResource resource = new ByteArrayResource(archivo.getBytes()) {
                @Override
                public String getFilename() {
                    return archivo.getOriginalFilename();
                }
            };

            // Construye el cuerpo de la solicitud multipart
            MultipartBodyBuilder builder = new MultipartBodyBuilder();
            builder.part("file", resource, MediaType.MULTIPART_FORM_DATA);

            // Realiza la llamada HTTP POST a la API de fotos
            return webClient.post() // Usa la instancia de WebClient del constructor
                    .uri(this.fotoServiceUrl + "/upload") // URL completa
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body((BodyInserter<?, ? super ClientHttpRequest>) builder.build()) // No se necesita castear
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error al comunicarse con la API de fotos: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo de la foto: " + e.getMessage(), e);
        }
    }
}