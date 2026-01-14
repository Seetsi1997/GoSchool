package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.Province;
import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.DriverDTO;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.dtos.StudentDTO;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.service.AuthService;
import com.example.GoSchool.service.DriverService;
import com.example.GoSchool.service.ParentService;
import com.twilio.jwt.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/api/drivers")
public class DriverController {

    private final AuthService authService;
    private final DriverService driverService;

    @Autowired
    public DriverController (DriverService parentService, AuthService authService){
        this.driverService = parentService;
        this.authService = authService;
    }

    // Register driver
    @PostMapping("/register")
    public ResponseEntity<?> registerDriver(@Valid @RequestBody DriverDTO driverDTO) {
        try {
            Users savedUser = authService.registerBaseUser(
                    driverDTO.getEmail(),
                    driverDTO.getDriverName() + " " + driverDTO.getDriverSurname(),
                    Role.DRIVER,
                    driverDTO.getPassword(),
                    driverDTO.getContact()
            );

            driverService.createDriver(driverDTO, savedUser.getUuid()); // now UUID exists

            return ResponseEntity.ok(Map.of("message", "Driver registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get driver by uuid
    @GetMapping("/me/{userUUID}")
    public ResponseEntity<?> getDriverByUser(@PathVariable UUID userUUID) {
        try {
            Driver driver = driverService.getDriverByUserId(userUUID);
            return ResponseEntity.ok(driver);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all drivers
    @GetMapping
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        List<DriverDTO> driverDTOs = drivers.stream()
                .map(driverService::getDriverToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(driverDTOs);
    }

    // Get current logged-in driver to get their profile
    @GetMapping("/profile")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<DriverDTO> getCurrentDriverProfile(Authentication authentication) {
        String email = authentication.getName();

        // Fetch the driver entity
        Driver driver = driverService.getDriverByEmail(email);

        // Use your existing service method to get DTO with total students
        DriverDTO driverDTO = driverService.getCurrentDriver(driver.getDriverUUID());

        return ResponseEntity.ok(driverDTO);
    }

    // Update current driver
    @PutMapping("/profile")
    public ResponseEntity<DriverDTO> updateCurrentDriverProfile(
            Authentication authentication,
            @RequestBody DriverDTO updatedDriverData
    ) {
        String email = authentication.getName();

        Driver updatedDriver = driverService.updateDriverByEmail(email, updatedDriverData);

        return ResponseEntity.ok(driverService.getDriverToDTO(updatedDriver));
    }

    // Get students by driver province
    @GetMapping("/{province}/students")
    public List<StudentDTO> getStudentsByProvince(@PathVariable Province province) {
        return driverService.getStudentsByDriverProvince(province)
                .stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }

    // Assign driver to the student by uuid
    @PostMapping("/driver/{driverId}/assign/student/{studentId}")
    public ResponseEntity<Void> assignDriver(
            @PathVariable UUID driverId,
            @PathVariable UUID studentId) {

        driverService.assignDriverToStudent(driverId, studentId);
        return ResponseEntity.ok().build();
    }


}
