package com.ablodich.smis.userservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                                    new SecurityScheme()
                                                            .type(SecurityScheme.Type.HTTP)
                                                            .scheme("bearer")
                                                            .bearerFormat("JWT")
                                                   )
                           )
                .security(Collections.singletonList(new SecurityRequirement().addList(securitySchemeName)));
    }
}
