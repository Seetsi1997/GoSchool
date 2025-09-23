package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Role;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String email;
    private String username;
    private String password;
    private Role role;
}
