package com.example.GoSchool.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ResetPasswordDTO {
    private String token;
    private String email;
    private String newPassword;
    private String confirmPassword;
    public boolean isPasswordMatch() {
        return newPassword != null && newPassword.equals(confirmPassword);
    }
}
