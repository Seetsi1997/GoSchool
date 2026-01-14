package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.DriverDTO;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.dtos.StudentDTO;
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
import com.example.GoSchool.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auth/api/users/admin")
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ParentService parentService;
    private final ParentMapper parentMapper;
    private final DriverService driverService;
    private final DriverMapper driverMapper;
    private final StudentService studentService;


    @Autowired
    public AdminController(AuthService authService,
                           UserRepository userRepository,
                           ParentService parentService,
                           ParentMapper parentMapper,
                           DriverMapper driverMapper,
                           DriverService driverService,
                           StudentService studentService) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.parentService = parentService;
        this.parentMapper = parentMapper;
        this.driverMapper = driverMapper;
        this.driverService = driverService;
        this.studentService = studentService;
    }

    // Register / Create admin for users
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

    // Get current admin profile
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/profile")
    public ResponseEntity<Users> getCurrentAdminProfile(Authentication authentication) {

        String email = authentication.getName();

        Users admin = authService.getAdminByEmail(email);

        return ResponseEntity.ok(admin);
    }

    // Get all drivers to the admin side
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/drivers")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<Driver> drivers = driverService.getAllDrivers();
        List<DriverDTO> dtoList = driverMapper.toDTOList(drivers);
        return ResponseEntity.ok(dtoList);
    }

    // Get all parents to the admin side
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/parents")
    public ResponseEntity<List<ParentDTO>> getAllParents() {
        List<Parent> parents = parentService.getAllParents();
        List<ParentDTO> dtos = parentMapper.toDTOList(parents);
        return ResponseEntity.ok(dtos);
    }

    // Get student by the drivers to the admin side
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/drivers/{driverUUID}/students")
    public ResponseEntity<List<StudentDTO>> getStudentsByDriver(
            @PathVariable UUID driverUUID
    ) {
        List<StudentDTO> students = studentService.getStudentsByDriver(driverUUID);
        return ResponseEntity.ok(students);
    }

}
