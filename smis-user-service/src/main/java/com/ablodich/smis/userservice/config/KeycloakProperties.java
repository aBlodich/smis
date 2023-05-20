package com.ablodich.smis.userservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smis.security.keycloak")
public class KeycloakProperties {
    private String baseUrl;
    private String realm;
    private String clientId;
    private String clientSecret;
}
