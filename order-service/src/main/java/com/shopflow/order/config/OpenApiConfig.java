package com.shopflow.order.config;

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
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("ShopFlow — Order Service API")
                .description("""
                    REST API for order management in ShopFlow distributed platform.
                    
                    **Saga Pattern Flow:**
                    ```
                    POST /api/orders
                      → OrderCreated (Kafka)
                        → Stock Service: reserve stock
                        → Payment Service: process payment
                          → PaymentCompleted → Order status: PAID
                          → PaymentFailed   → Order status: CANCELLED
                    ```
                    
                    **Resilience4j:** Circuit breaker on Product Service calls.
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
                    .bearerFormat("JWT")));
    }
}
