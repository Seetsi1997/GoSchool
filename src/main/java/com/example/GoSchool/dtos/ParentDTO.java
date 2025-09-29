package com.example.GoSchool.dtos;

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
    private String contacts;
    private LocationDTO parentLocation;
    private UUID userId;
    private String email;
    private List<StudentDTO> studentDTOList;
    private String password;
}
