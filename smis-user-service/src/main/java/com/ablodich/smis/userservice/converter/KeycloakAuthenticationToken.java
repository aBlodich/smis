package com.ablodich.smis.userservice.converter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;

import java.util.Collection;
import java.util.Map;

public class KeycloakAuthenticationToken extends AbstractOAuth2TokenAuthenticationToken<Jwt> {
    private final String name;


    protected KeycloakAuthenticationToken(final Jwt token) {
        super(token);
        this.name = token.getSubject();
    }

    protected KeycloakAuthenticationToken(final Jwt token, final Collection<? extends GrantedAuthority> authorities) {
        super(token, authorities);
        this.setAuthenticated(true);
        this.name = token.getSubject();
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.getToken().getClaims();
    }

    /**
     * The principal name which is, by default, the {@link Jwt}'s subject
     */
    @Override
    public String getName() {
        return this.name;
    }
}
