package com.SAFE_Rescue.API_Perfiles.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        //http://localhost:8080/doc/swagger-ui/index.html
        return new OpenAPI()
                .info(new Info()
                        .title("API 2025 Creación y gestión de usuarios")
                        .version("1.0")
                        .description("Documentación de la API para el sistema de creación y gestión de usuarios"));
    }
}
