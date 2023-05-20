package com.ablodich.smis.userservice.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class GetUserDto {
    private UUID id;
    private String username;
    private String email;
    private String lastName;
    private String firstName;
    private String secondName;
}
