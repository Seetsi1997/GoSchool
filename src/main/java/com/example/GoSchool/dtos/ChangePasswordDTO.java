package com.example.GoSchool.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}