package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String firstName;
    private String phoneNumber;
    private String profileImageUrl;
    private Role role;
    private UUID parentUUID;
    private String parentFirstName;

}
