package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Province;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    private UUID driverUUID;
    private String firstName;
    private String surname;
    private UUID locationUUID;
    private String city;
    private Province province;
    private String address;
    private String postalCode;
    private String suburb;
    private int totalNumberOfStudents;
    private List<StudentDTO> assignedStudents;
    private UUID userId;
    private String email;
    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^((\\+27|27)|0)[5-9]\\d{8}$",
            message = "Invalid South African phone number format. Use: 0712345678 or +27712345678")
    private String contact;
    private String password;
}

