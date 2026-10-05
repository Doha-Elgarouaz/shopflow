package com.shopflow.product.config;

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
    public OpenAPI productServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ShopFlow — Product Service API")
                .description("""
                    REST API for product management in ShopFlow distributed platform.
                    
                    **Features:**
                    - Product CRUD with Redis caching
                    - Category-based filtering with pagination
                    - Stock management
                    - Kafka event publishing
                    
                    **Auth:** Bearer JWT from Keycloak (realm: `shopflow`)
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("ShopFlow Team")
                    .url("https://github.com/Doha-Elgarouaz/shopflow"))
                .license(new License().name("MIT")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Enter JWT token from Keycloak")));
    }
}
