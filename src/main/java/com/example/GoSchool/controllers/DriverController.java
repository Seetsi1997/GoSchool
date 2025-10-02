package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.DriverDTO;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.service.AuthService;
import com.example.GoSchool.service.DriverService;
import com.example.GoSchool.service.ParentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/api/drivers")
public class DriverController {

    private final AuthService authService;
    private final DriverService driverService;

    public DriverController (DriverService parentService, AuthService authService){
        this.driverService = parentService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerParent(@RequestBody DriverDTO driverDTO) {
        try {
            Users savedUser = authService.registerBaseUser(
                    driverDTO.getEmail(),
                    driverDTO.getFirstName() + " " + driverDTO.getSurname(),
                    Role.DRIVER,
                    driverDTO.getPassword()
            );

            driverService.createDriver(driverDTO, savedUser.getUuid()); // now UUID exists

            return ResponseEntity.ok(Map.of("message", "Driver registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
