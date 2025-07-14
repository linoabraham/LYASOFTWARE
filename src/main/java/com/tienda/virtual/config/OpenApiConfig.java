// src/main/java/com/tienda/virtual/config/OpenApiConfig.java
package com.tienda.virtual.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("API de Tienda Virtual")
                        .version("1.0")
                        .description("Documentación de la API de la Tienda Virtual, incluyendo gestión de usuarios, servicios, órdenes y transacciones."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        ))
                .servers(List.of( // <-- Ahora Java sabrá qué es 'List'
                        new Server().url("https://smart-meridith-consultingrl-f43089a8.koyeb.app"), // <-- y qué es 'Server'
                        new Server().url("http://localhost:8080")

                ));
    }
}
