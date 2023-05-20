package com.ablodich.smis.userservice.service;

import com.ablodich.smis.userservice.dto.GetUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakSsoProviderService implements SsoProviderService {
    private final Keycloak keycloak;

    @Override
    public GetUserDto getUserById(final UUID userId) {
        UserRepresentation userRepresentation = keycloak.realm("smis-realm").users().get(userId.toString()).toRepresentation();
        GetUserDto getUserDto = new GetUserDto();
        getUserDto.setId(UUID.fromString(userRepresentation.getId()));
        getUserDto.setUsername(userRepresentation.getUsername());
        getUserDto.setEmail(userRepresentation.getEmail());
        getUserDto.setFirstName(userRepresentation.getFirstName());
        getUserDto.setLastName(userRepresentation.getLastName());
        return getUserDto;
    }
}
