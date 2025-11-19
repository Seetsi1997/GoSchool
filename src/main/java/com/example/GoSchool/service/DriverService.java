package com.example.GoSchool.service;

import com.example.GoSchool.dtos.DriverDTO;
import com.example.GoSchool.dtos.StudentDTO;
import com.example.GoSchool.dtos.LocationDTO;
import com.example.GoSchool.model.*;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.DriverRepository;
import com.example.GoSchool.repository.LocationRepository;
import com.example.GoSchool.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private  final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public DriverService(UserRepository userRepository, DriverRepository driverRepository,
                         LocationRepository locationRepository){
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.locationRepository = locationRepository;
    }

    public void createDriver(@Valid DriverDTO driverDTO, UUID userId) {
        // Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Handle location safely
        Location location;
        if (driverDTO.getDriverLocation().getLocationUUID() != null) {
            // Existing location
            location = locationRepository.findById(driverDTO.getDriverLocation().getLocationUUID())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + driverDTO.getDriverLocation().getLocationUUID()));
        } else {
            // Create new location
            Location newLoc = new Location();
            newLoc.setCity(driverDTO.getDriverLocation().getCity());
            newLoc.setProvince(driverDTO.getDriverLocation().getProvince());
            newLoc.setAddress(driverDTO.getDriverLocation().getAddress());
            newLoc.setPostalCode(driverDTO.getDriverLocation().getPostalCode());
            newLoc.setSuburb(driverDTO.getDriverLocation().getSuburb());
            location = locationRepository.save(newLoc);
        }

        // Create driver (validation is now handled by @Valid)
        Driver driver = new Driver();
        driver.setDriverUUID(driverDTO.getDriverUUID());
        driver.setDriverName(driverDTO.getDriverName());
        driver.setDriverSurname(driverDTO.getSurname());
        driver.setDriverLocation(location);
        driver.setContact(driverDTO.getContact());
        driver.setTotalNumberOfStudents(0);
        driver.setUserAccount(user);

        // Map students
        List<Student> students = driverDTO.getAssignedStudents() != null
                ? driverDTO.getAssignedStudents().stream()
                .map(studentDTO -> {
                    Student student = new Student();
                    if (studentDTO.getStudentUUID() != null) {
                        student.setStudentUUID(studentDTO.getStudentUUID());
                    }
                    student.setStudentFirstName(studentDTO.getStudentFirstName());
                    student.setStudentSurname(studentDTO.getStudentSurname());
                    student.setStudentGrade(studentDTO.getStudentGrade());
                    student.setMonthlyPaymentAmount(studentDTO.getMonthlyPaymentAmount());
                    student.setPaymentStatus(studentDTO.getPaymentStatus());
                    student.setDriver(driver);
                    return student;
                })
                .collect(Collectors.toList())
                : new ArrayList<>();

        driver.setAssignedStudents(students);

        driverRepository.save(driver);
    }

    // Get driver by UUID
    public Driver getDriverById(UUID driverUUID) {
        return driverRepository.findById(driverUUID)
                .orElseThrow(() -> new RuntimeException("Driver not found with id: " + driverUUID));
    }

    // Get all drivers
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    // Update driver details
    public Driver updateDriver(UUID driverUUID, Driver updatedDriver) {
        Driver existingDriver = getDriverById(driverUUID);

        existingDriver.setDriverName(updatedDriver.getDriverName());
        existingDriver.setDriverSurname(updatedDriver.getDriverSurname());
        existingDriver.setDriverLocation(updatedDriver.getDriverLocation());
        existingDriver.setTotalNumberOfStudents(updatedDriver.getTotalNumberOfStudents());
        existingDriver.setContact(updatedDriver.getContact());
        // Optional: update assigned students if needed
        existingDriver.setAssignedStudents(updatedDriver.getAssignedStudents());

        return driverRepository.save(existingDriver);
    }

    // Delete a driver
    public void deleteDriver(UUID driverUUID) {
        Driver driver = getDriverById(driverUUID);
        driverRepository.delete(driver);
    }

    // Find driver by linked user account
    public Driver getDriverByUserId(UUID userId) {
        return driverRepository.findByUserAccountUuid(userId)
                .orElseThrow(() -> new RuntimeException("Driver not found for userId: " + userId));
    }

    public Driver getDriverByEmail(String email) {
        return  driverRepository.findByUserAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found with email: " + email));
    }

    public DriverDTO getDriverToDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();

        dto.setDriverUUID(driver.getDriverUUID());
        dto.setDriverName(driver.getDriverName());
        dto.setSurname(driver.getDriverSurname());
        dto.setContact(driver.getContact());
        dto.setEmail(driver.getUserAccount().getEmail());
        dto.setRole(driver.getUserAccount().getRole());
        dto.setUserId(driver.getUserAccount().getUuid());
        dto.setTotalNumberOfStudents(driver.getTotalNumberOfStudents());

        // Map location properly
        if (driver.getDriverLocation() != null) {
            LocationDTO locationDTO = new LocationDTO(
                    driver.getDriverLocation().getLocationUUID(),
                    driver.getDriverLocation().getCity(),
                    driver.getDriverLocation().getAddress(),
                    driver.getDriverLocation().getPostalCode(),
                    driver.getDriverLocation().getProvince(),
                    driver.getDriverLocation().getSuburb()
            );
            dto.setDriverLocation(locationDTO);
        }

        // Map assigned students
        if (driver.getAssignedStudents() != null) {
            dto.setAssignedStudents(
                    driver.getAssignedStudents()
                            .stream()
                            .map(StudentDTO::new)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setAssignedStudents(new ArrayList<>());
        }

        return dto;
    }


}
