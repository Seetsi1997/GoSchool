package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Province;
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
    private int totalNumberOfStudents;
    private List<StudentDTO> assignedStudents;
    private UUID userId;
    private String email;
    private String contact;
    private String password;
}

