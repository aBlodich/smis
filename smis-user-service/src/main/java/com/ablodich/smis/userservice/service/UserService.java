package com.ablodich.smis.userservice.service;

import com.ablodich.smis.common.exceptions.InternalErrorException;
import com.ablodich.smis.userservice.converter.KeycloakAuthenticationToken;
import com.ablodich.smis.userservice.dto.GetUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final SsoProviderService ssoProviderService;

    public GetUserDto getUserById(final UUID userId) {
        return ssoProviderService.getUserById(userId);
    }

    public GetUserDto getCurrentUser(final Authentication authentication) {
        if (!(authentication instanceof KeycloakAuthenticationToken token)) {
            throw new InternalErrorException("Не получилось получить информацию по пользователе из-за внутренней ошибки");
        }
        String currentUserId = token.getToken().getSubject();
        return getUserById(UUID.fromString(currentUserId));
    }
}
