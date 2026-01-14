package com.example.GoSchool.mapper;

import com.example.GoSchool.dtos.*;
import com.example.GoSchool.model.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DriverMapper {

    /* =========================
       ENTITY -> DTO (READ)
       ========================= */
    public DriverDTO toDTO(Driver driver) {
        if (driver == null) return null;

        DriverDTO dto = new DriverDTO();
        dto.setDriverUUID(driver.getDriverUUID());
        dto.setDriverName(driver.getDriverName());
        dto.setDriverSurname(driver.getDriverSurname());
        dto.setTotalNumberOfStudents(driver.getTotalNumberOfStudents());
        dto.setContact(driver.getContact());

        if (driver.getUserAccount() != null) {
            dto.setEmail(driver.getUserAccount().getEmail());
            dto.setUserId(driver.getUserAccount().getUuid());
            dto.setRole(driver.getUserAccount().getRole());
        }

        if (driver.getDriverLocation() != null) {
            dto.setDriverLocation(new LocationDTO(
                    driver.getDriverLocation().getLocationUUID(),
                    driver.getDriverLocation().getCity(),
                    driver.getDriverLocation().getAddress(),
                    driver.getDriverLocation().getPostalCode(),
                    driver.getDriverLocation().getProvince(),
                    driver.getDriverLocation().getSuburb()
            ));
        }

        // Assigned students (READ ONLY)
        if (driver.getAssignedStudents() != null && !driver.getAssignedStudents().isEmpty()) {
            List<StudentDTO> students = driver.getAssignedStudents()
                    .stream()
                    .map(StudentDTO::new)
                    .toList();
            dto.setAssignedStudents(students);
        }

        // Route details
        if (driver.getRouteDetails() != null && !driver.getRouteDetails().isEmpty()) {
            List<DriverRouteDetailsDTO> routes = driver.getRouteDetails()
                    .stream()
                    .map(DriverRouteDetailsDTO::new)
                    .toList();
            dto.setRouteDetails(routes);
        }

        return dto;
    }

    public List<DriverDTO> toDTOList(List<Driver> drivers) {
        return drivers.stream().map(this::toDTO).toList();
    }

    /* =========================
       DTO -> ENTITY (WRITE)
       ========================= */
    public Driver toEntity(DriverDTO dto) {
        if (dto == null) return null;

        Driver driver = new Driver();
        driver.setDriverUUID(dto.getDriverUUID());
        driver.setDriverName(dto.getDriverName());
        driver.setDriverSurname(dto.getDriverSurname());
        driver.setTotalNumberOfStudents(dto.getTotalNumberOfStudents());
        driver.setContact(dto.getContact());

        if (dto.getUserId() != null) {
            Users user = new Users();
            user.setUuid(dto.getUserId());
            user.setEmail(dto.getEmail());
            user.setRole(dto.getRole());
            driver.setUserAccount(user);
        }

        if (dto.getDriverLocation() != null) {
            Location location = new Location();
            location.setLocationUUID(dto.getDriverLocation().getLocationUUID());
            location.setCity(dto.getDriverLocation().getCity());
            location.setAddress(dto.getDriverLocation().getAddress());
            location.setPostalCode(dto.getDriverLocation().getPostalCode());
            location.setProvince(dto.getDriverLocation().getProvince());
            location.setSuburb(dto.getDriverLocation().getSuburb());
            driver.setDriverLocation(location);
        }

        return driver;
    }
}
