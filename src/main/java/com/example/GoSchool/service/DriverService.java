package com.example.GoSchool.service;

import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.DriverRepository;
import com.example.GoSchool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DriverService {

    private  final UserRepository userRepository;
    private final DriverRepository driverRepository;

    @Autowired
    public DriverService(UserRepository userRepository, DriverRepository driverRepository){
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
    }

    public Driver create(Driver driver, UUID userUUID){

            // Fetch the associated user
            Users user = userRepository.findById(userUUID)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + userUUID));

            // Link the user to the driver
            driver.setUserAccount(user);

            return driverRepository.save(driver);
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
