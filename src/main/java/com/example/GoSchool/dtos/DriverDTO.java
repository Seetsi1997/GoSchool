package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.Province;
import com.example.GoSchool.constant.Role;
import com.example.GoSchool.model.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
    private UUID driverUUID;
    private String driverName;
    private String surname;
    private String email;
    private String contact;
    private String password;
    private LocationDTO driverLocation;
    private int totalNumberOfStudents;
    private List<StudentDTO> assignedStudents = new ArrayList<>();
    private UUID userId;
    private Role role;

    // Optional flag to prevent recursion when mapping students → driver
    public DriverDTO(Driver driver) {
        this(driver, true);
    }

    public DriverDTO(Driver driver, boolean includeStudents) {
        if(driver == null) return;

        this.driverUUID = driver.getDriverUUID();
        this.driverName = driver.getDriverName();
        this.surname = driver.getDriverSurname();
        this.contact = driver.getContact();
        this.email = driver.getUserAccount() != null ? driver.getUserAccount().getEmail() : null;
        this.userId = driver.getUserAccount() != null ? driver.getUserAccount().getUuid() : null;
        this.role = driver.getUserAccount() != null ? driver.getUserAccount().getRole() : null;
        this.totalNumberOfStudents = driver.getTotalNumberOfStudents();

        if(driver.getDriverLocation() != null) {
            this.driverLocation = new LocationDTO(
                    driver.getDriverLocation().getLocationUUID(),
                    driver.getDriverLocation().getCity(),
                    driver.getDriverLocation().getAddress(),
                    driver.getDriverLocation().getPostalCode(),
                    driver.getDriverLocation().getProvince(),
                    driver.getDriverLocation().getSuburb()
            );
        }

        if(includeStudents && driver.getAssignedStudents() != null) {
            this.assignedStudents = driver.getAssignedStudents()
                    .stream()
                    .map(StudentDTO::new)
                    .collect(Collectors.toList());
        }
    }
}
