package com.ablodich.smis.userservice.converter;

import org.springframework.security.core.GrantedAuthority;

public class KeycloakGrantedAuthority implements GrantedAuthority {
    private final String authority;

    public KeycloakGrantedAuthority(final String authority) {
        this.authority = setAuthority(authority);
    }

    private String setAuthority(final String authority) {
        String authorityUpperCase = authority.toUpperCase();
        if (authorityUpperCase.startsWith("ROLE_")) {
            return authorityUpperCase;
        }
        return "ROLE_" + authorityUpperCase;
    }

    @Override
    public String getAuthority() {
        return authority;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        KeycloakGrantedAuthority that = (KeycloakGrantedAuthority) o;

        return authority.equals(that.authority);
    }

    @Override
    public int hashCode() {
        return authority.hashCode();
    }
}
