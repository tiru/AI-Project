package com.cargo.onerecord.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cargo ONE Record API")
                        .description("""
                                Airline Cargo Management System based on IATA ONE Record Standard (v2.0.0).

                                ONE Record is a standard for digital logistics and transport supply chain
                                data sharing. This API implements the ONE Record data model and API
                                specification without dependency on any external server.

                                **Roles:**
                                - `ADMIN` — Full access (create, read, update, delete)
                                - `OPERATOR` — Create and update logistics objects and events
                                - `VIEWER` — Read-only access
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Cargo ONE Record Team")
                                .email("cargo@example.com"))
                        .license(new License()
                                .name("IATA ONE Record")
                                .url("https://www.iata.org/one-record")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter JWT token from /auth/login")));
    }
}