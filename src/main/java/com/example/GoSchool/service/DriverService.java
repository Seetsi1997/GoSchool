package com.example.GoSchool.service;

import com.example.GoSchool.dtos.DriverDTO;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.model.*;
import com.example.GoSchool.repository.DriverRepository;
import com.example.GoSchool.repository.LocationRepository;
import com.example.GoSchool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Driver create(Driver driver, UUID userUUID){

            // Fetch the associated user
            Users user = userRepository.findById(userUUID)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userUUID));

            // Link the user to the driver
            driver.setUserAccount(user);

            return driverRepository.save(driver);
    }
    public void createDriver(DriverDTO driverDTO, UUID userId) {
        // Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Handle location safely
        Location location;
        UUID locationId = driverDTO.getDriverLocation().getLocationUUID();
        if (locationId != null) {
            location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));
        } else {
            Location newLoc = new Location();
            newLoc.setCity(driverDTO.getDriverLocation().getCity());
            newLoc.setProvince(driverDTO.getDriverLocation().getProvince());
            newLoc.setAddress(driverDTO.getDriverLocation().getAddress());
            newLoc.setPostalCode(driverDTO.getDriverLocation().getPostalCode());
            location = locationRepository.save(newLoc);
        }

        // Create driver first (so we can set it in students)
        Driver driver = new Driver();
        driver.setDriverUUID(driverDTO.getDriverUUID());
        driver.setDriverName(driverDTO.getDriverName());
        driver.setDriverSurname(driverDTO.getDriverSurname());
        driver.setDriverLocation(location);
        driver.setDriverContact(driverDTO.getDriverContact());
        driver.setTotalNumberOfStudents(0);
        driver.setUserAccount(user);

        // Map StudentDTO -> Student and set driver
        List<Student> students = driverDTO.getAssignedStudents().stream()
                .map(studentDTO -> {
                    Student student = new Student();
                    // Only set UUID if it exists
                    if (studentDTO.getStudentUUID() != null) {
                        student.setStudentUUID(studentDTO.getStudentUUID());
                    }
                    student.setStudentFirstName(studentDTO.getStudentFirstName());
                    student.setStudentSurname(studentDTO.getStudentSurname());
                    student.setStudentGrade(studentDTO.getStudentGrade());
                    student.setMonthlyPaymentAmount(studentDTO.getMonthlyPaymentAmount());
                    student.setPaymentStatus(studentDTO.getPaymentStatus());
                    student.setDriver(driver); // important!
                    return student;
                })
                .collect(Collectors.toList());

        driver.setAssignedStudents(students);

        // Save driver (students will cascade if mapped with CascadeType.ALL)
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
        existingDriver.setDriverContact(updatedDriver.getDriverContact());
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

}
