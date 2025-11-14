package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Province;
import com.example.GoSchool.constant.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentDTO {
    private UUID parentUUID;
    private String firstName;
    private String surname;
    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^((\\+27|27)|0)[5-9]\\d{8}$",
            message = "Invalid South African phone number format. Use: 0712345678 or +27712345678")
    private String contact;
   // private LocationDTO parentLocation;
    private UUID locationUUID;
    private String city;
    private String address;
    private String postalCode;
    private String suburb;
    private Province province;
    private UUID userId;
    private String email;
    private List<StudentDTO> studentDTOList;
    private String password;
    private Role role;
}
