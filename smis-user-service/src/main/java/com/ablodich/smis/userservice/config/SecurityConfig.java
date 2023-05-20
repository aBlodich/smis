package com.ablodich.smis.userservice.config;

import com.ablodich.smis.userservice.converter.KeycloakAuthenticationConverter;
import com.ablodich.smis.userservice.converter.KeycloakAuthoritiesConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private static final String[] AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "v2/api-docs",
            "/swagger-resources",
            "swagger-resources",
            "/swagger-resources/**",
            "swagger-resources/**",
            "/configuration/ui",
            "configuration/ui",
            "/configuration/security",
            "configuration/security",
            "/swagger-ui.html",
            "swagger-ui.html",
            "webjars/**",
            // -- Swagger UI v3
            "/v3/api-docs/**",
            "v3/api-docs/**",
            "/swagger-ui/**",
            "swagger-ui/**",
            // CSA Controllers
            "/csa/api/token",
            // Actuators
            "/actuator/**",
            "/health/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                    .requestMatchers(AUTH_WHITELIST).permitAll()
                    .anyRequest().authenticated())
                .formLogin(AbstractHttpConfigurer::disable)
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(configurer -> configurer.jwt().jwtAuthenticationConverter(getConverter()));
        return http.build();
    }

    private Converter<Jwt,? extends AbstractAuthenticationToken> getConverter() {
        return new KeycloakAuthenticationConverter(new KeycloakAuthoritiesConverter());
    }
}
