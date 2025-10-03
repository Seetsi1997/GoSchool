package com.example.GoSchool.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponse {
    private String token;
    private String role;
    //private String email;
    private  String phoneNumber;
    private String firstName;
    private UUID uuid;
}
