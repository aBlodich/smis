package com.ablodich.smis.userservice.service;

import com.ablodich.smis.userservice.dto.GetUserDto;

import java.util.UUID;

public interface SsoProviderService {

    GetUserDto getUserById(UUID userId);
}
