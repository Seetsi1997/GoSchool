package com.example.GoSchool.dtos;

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
    private String driverName;
    private String driverSurname;
    private LocationDTO driverLocation;
    private int totalNumberOfStudents;
    private List<StudentDTO> assignedStudents;
    private UUID userId;
    private String email;
    private String driverContact;
    private String password;
}

