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
        // 1. Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // 2. Handle location safely
        Location location = null;
        if (driverDTO.getDriverLocation() != null) {
            LocationDTO locDTO = driverDTO.getDriverLocation();
            if (locDTO.getLocationUUID() != null) {
                // Existing location
                location = locationRepository.findById(locDTO.getLocationUUID())
                        .orElseThrow(() -> new RuntimeException("Location not found with id: " + locDTO.getLocationUUID()));
            } else if (locDTO.getCity() != null || locDTO.getAddress() != null) {
                // Create new location if at least one field is provided
                Location newLoc = new Location();
                newLoc.setCity(locDTO.getCity());
                newLoc.setProvince(locDTO.getProvince());
                newLoc.setAddress(locDTO.getAddress());
                newLoc.setPostalCode(locDTO.getPostalCode());
                newLoc.setSuburb(locDTO.getSuburb());
                location = locationRepository.save(newLoc);
            }
        }

        // 3. Create driver
        Driver driver = new Driver();
        driver.setDriverUUID(driverDTO.getDriverUUID());
        driver.setDriverName(driverDTO.getDriverName());
        driver.setDriverSurname(driverDTO.getDriverSurname());
        driver.setDriverLocation(location);
        driver.setContact(driverDTO.getContact());
        driver.setTotalNumberOfStudents(0);
        driver.setUserAccount(user);

        // 4. Map students safely
        List<Student> students = new ArrayList<>();
        if (driverDTO.getAssignedStudents() != null) {
            for (StudentDTO sDTO : driverDTO.getAssignedStudents()) {
                Student student = new Student();
                if (sDTO.getStudentUUID() != null) {
                    student.setStudentUUID(sDTO.getStudentUUID());
                }
                student.setStudentFirstName(sDTO.getStudentFirstName());
                student.setStudentSurname(sDTO.getStudentSurname());
                student.setStudentGrade(sDTO.getStudentGrade());
                student.setMonthlyPaymentAmount(sDTO.getMonthlyPaymentAmount());
                student.setPaymentStatus(sDTO.getPaymentStatus());
                student.setDriver(driver);
                students.add(student);
            }
        }
        driver.setAssignedStudents(students);

        // 5. Save driver
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

    public Driver updateDriverByEmail(String email, DriverDTO dto) {
        Driver existingDriver = getDriverByEmail(email);

        existingDriver.setDriverName(dto.getDriverName());
        existingDriver.setDriverSurname(dto.getDriverSurname());
        existingDriver.setTotalNumberOfStudents(dto.getTotalNumberOfStudents());
        existingDriver.setContact(dto.getContact());

        Location location = existingDriver.getDriverLocation();

        // Location
        if (location != null) {
            location.setAddress(dto.getDriverLocation().getAddress());
            location.setSuburb(dto.getDriverLocation().getSuburb());
            location.setCity(dto.getDriverLocation().getCity());
            location.setPostalCode(dto.getDriverLocation().getPostalCode());
            location.setProvince(dto.getDriverLocation().getProvince());
        }else {
        Location newLocation = new Location();
        newLocation.setAddress(dto.getDriverLocation().getAddress());
        newLocation.setSuburb(dto.getDriverLocation().getSuburb());
        newLocation.setCity(dto.getDriverLocation().getCity());
        newLocation.setPostalCode(dto.getDriverLocation().getPostalCode());
        newLocation.setProvince(dto.getDriverLocation().getProvince());

        locationRepository.save(newLocation);
        existingDriver.setDriverLocation(newLocation);
    }


        // Assigned Students
        if (dto.getAssignedStudents() != null) {

            // Clear the existing list WITHOUT replacing the list instance
            existingDriver.getAssignedStudents().clear();

            // Add new list items
            for (StudentDTO s : dto.getAssignedStudents()) {
                Student student = s.toEntity();  // must at least have UUID
                student.setDriver(existingDriver);  // maintain relationship
                existingDriver.getAssignedStudents().add(student);
            }
        }

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
        dto.setDriverSurname(driver.getDriverSurname());
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
