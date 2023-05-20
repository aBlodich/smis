package com.ablodich.smis.userservice.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.stream.Collectors;

public class KeycloakAuthoritiesConverter implements Converter<Jwt, Collection<? extends GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends GrantedAuthority> convert(final Jwt source) {
        var roles = (Collection<String>) source.getClaimAsMap("realm_access").get("roles");
        return roles.stream().map(KeycloakGrantedAuthority::new).distinct().collect(Collectors.toList());
    }
}
