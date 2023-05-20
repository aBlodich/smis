package com.ablodich.smis.userservice.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

@RequiredArgsConstructor
public class KeycloakAuthenticationConverter  implements Converter<Jwt, AbstractAuthenticationToken> {
    private final KeycloakAuthoritiesConverter keycloakAuthoritiesConverter;

    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        var authorities = keycloakAuthoritiesConverter.convert(source);
        return new KeycloakAuthenticationToken(source, authorities);
    }
}
