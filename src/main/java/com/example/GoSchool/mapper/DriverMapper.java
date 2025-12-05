package com.example.GoSchool.mapper;

import com.example.GoSchool.dtos.*;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.Parent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DriverMapper {

    public DriverDTO toDTO(Driver driver) {
        if (driver == null) return null;

        DriverDTO driverDTO = new DriverDTO();

        driverDTO.setDriverUUID(driver.getDriverUUID());
        driverDTO.setDriverName(driver.getDriverName());
        driverDTO.setDriverSurname(driver.getDriverSurname());
        driverDTO.setTotalNumberOfStudents(driver.getTotalNumberOfStudents());
        driverDTO.setEmail(driver.getUserAccount() != null ? driver.getUserAccount().getEmail() : null);
        driverDTO.setContact(driver.getContact());
        driverDTO.setUserId(driver.getUserAccount() != null ? driver.getUserAccount().getUuid() : null);
        driverDTO.setRole(driver.getUserAccount() != null ? driver.getUserAccount().getRole() : null);

        // Location mapping
        if(driver.getDriverLocation() != null) {
            driverDTO.setDriverLocation(new LocationDTO(
                    driver.getDriverLocation().getLocationUUID(),
                    driver.getDriverLocation().getCity(),
                    driver.getDriverLocation().getAddress(),
                    driver.getDriverLocation().getPostalCode(),
                    driver.getDriverLocation().getProvince(),
                    driver.getDriverLocation().getSuburb()
            ));
        }

        // Assigned students
        if(driver.getAssignedStudents() != null) {
            List<StudentDTO> studentDTOs = driver.getAssignedStudents().stream()
                    .map(StudentDTO::new)
                    .collect(Collectors.toList());
            driverDTO.setAssignedStudents(studentDTOs);
        }

        // Route details
        if(driver.getRouteDetails() != null) {
            List<DriverRouteDetailsDTO> routeDTOs = driver.getRouteDetails().stream()
                    .map(DriverRouteDetailsDTO::new)
                    .collect(Collectors.toList());
            driverDTO.setRouteDetails(routeDTOs);
        }

        return driverDTO;
    }

    public List<DriverDTO> toDTOList(List<Driver> drivers) {
        return drivers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
