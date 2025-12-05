package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.DriverDTO;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.dtos.UserRegistrationDTO;
import com.example.GoSchool.mapper.DriverMapper;
import com.example.GoSchool.mapper.ParentMapper;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.UserRepository;
import com.example.GoSchool.service.AuthService;
import com.example.GoSchool.service.DriverService;
import com.example.GoSchool.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/auth/api/users/admin")
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ParentService parentService;
    private final ParentMapper parentMapper;
    private final DriverService driverService;
    private final DriverMapper driverMapper;


    @Autowired
    public AdminController(AuthService authService,
                           UserRepository userRepository,
                           ParentService parentService,
                           ParentMapper parentMapper,
                           DriverMapper driverMapper,
                           DriverService driverService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.parentService = parentService;
        this.parentMapper = parentMapper;
        this.driverMapper = driverMapper;
        this.driverService = driverService;
    }

    @PostMapping("/register")
    public ResponseEntity<Users> createAdmin(@Valid @RequestBody UserRegistrationDTO request) {
        Users admin = authService.registerBaseUser(
                request.getEmail(),
                request.getFirstName(),
                request.getRole(),
                request.getPassword(),
                request.getPhoneNumber()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(admin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<Users> getCurrentAdminProfile(Authentication authentication) {

        String email = authentication.getName();

        Users admin = authService.getAdminByEmail(email);

        return ResponseEntity.ok(admin);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/drivers")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        List<DriverDTO> dtoList = driverMapper.toDTOList(drivers);
        return ResponseEntity.ok(dtoList);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/parents")
    public ResponseEntity<List<ParentDTO>> getAllParents() {
        List<Parent> parents = parentService.getAllParents();
        List<ParentDTO> dtos = parentMapper.toDTOList(parents);
        return ResponseEntity.ok(dtos);
    }

}
