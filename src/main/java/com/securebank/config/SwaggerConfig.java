package com.securebank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI secureBankOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SecureBank API")
                        .description(
                            "Production-grade Banking Management System API. " +
                            "Supports multi-role access: ADMIN, MANAGER, TELLER, CUSTOMER."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Vishal")
                                .email("vishalbhumkar12@gmail.com")
                                .url("https://github.com/vishalbhumkar"))
                        .license(new License()
                                .name("Educational Use")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description(
                                            "Enter JWT token from /api/v1/auth/login")));
    }
}