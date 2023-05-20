package com.ablodich.smis.userservice.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakConfig {
    private final KeycloakProperties properties;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder().serverUrl(properties.getBaseUrl())
                .realm(properties.getRealm())
                .clientId(properties.getClientId())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientSecret(properties.getClientSecret()).build();
    }
}
