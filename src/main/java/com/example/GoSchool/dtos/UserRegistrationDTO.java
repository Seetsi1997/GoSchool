package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Role;
import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String email;
    private String username;
    private Role role;
    private String password;
    private String confirmPassword;

    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
