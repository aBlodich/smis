package com.ablodich.smis.userservice.controller;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.userservice.dto.GetUserDto;
import com.ablodich.smis.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

/*    public ApiResponseDto<Page<UserDto>> getUsers(Pageable pageable) {

    }*/

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ApiResponseDto<GetUserDto> getUser(@PathVariable("userId")UUID userId) {
        return ApiResponseDto.ok(userService.getUserById(userId));
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ApiResponseDto<GetUserDto> getCurrentUser(Authentication authentication) {
        return ApiResponseDto.ok(userService.getCurrentUser(authentication));
    }
}
