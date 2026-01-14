package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.ApplicationStatus;
import com.example.GoSchool.dtos.TransportApplicationDTO;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.TransportApplication;
import com.example.GoSchool.repository.DriverRepository;
import com.example.GoSchool.service.TransportApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/auth/api/transport")
public class TransportApplicationController {
    private final TransportApplicationService service;
    private final DriverRepository driverRepository;

    @Autowired
    public TransportApplicationController(TransportApplicationService service,
                                          DriverRepository driverRepository) {
        this.service = service;
        this.driverRepository = driverRepository;
    }

    // Apply transport for student by parent
    @PreAuthorize("hasRole('PARENT')")
    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody TransportApplicationDTO dto) {

        if (dto.getParentId() == null) {
            return ResponseEntity.badRequest().body("Parent ID is required");
        }

        if (dto.getRouteId() == null) {
            return ResponseEntity.badRequest().body("Route ID is required");
        }

        if (dto.getStudentUUIDs() == null || dto.getStudentUUIDs().isEmpty()) {
            return ResponseEntity.badRequest().body("At least one student UUID is required");
        }

        List<TransportApplication> existingApps =
                service.getApplicationsByParentAndRoute(dto.getParentId(), dto.getRouteId());

        List<Map<String, Object>> results = new ArrayList<>();
        boolean hasBlocked = false;

        for (UUID studentUUID : dto.getStudentUUIDs()) {

            Optional<TransportApplication> existingAppForStudent = existingApps.stream()
                    .filter(a -> a.getStudent().getStudentUUID().equals(studentUUID))
                    .findFirst();

            if (existingAppForStudent.isPresent()) {
                TransportApplication app = existingAppForStudent.get();

                if (app.getApplicationStatus() == ApplicationStatus.PENDING ||
                        app.getApplicationStatus() == ApplicationStatus.APPROVED_BY_DRIVER ||
                        app.getApplicationStatus() == ApplicationStatus.APPROVED_BY_ADMIN) {

                    hasBlocked = true;

                    results.add(Map.of(
                            "studentUUID", studentUUID,
                            "status", "blocked",
                            "message", "Already applied for this student on this route"
                    ));
                    continue;
                }

                if (app.getApplicationStatus() == ApplicationStatus.DECLINED) {
                    service.deleteApplication(app.getApplicationId());
                }
            }

            TransportApplicationDTO singleStudentDto = new TransportApplicationDTO();
            singleStudentDto.setParentId(dto.getParentId());
            singleStudentDto.setRouteId(dto.getRouteId());
            singleStudentDto.setStudentUUIDs(List.of(studentUUID));
            singleStudentDto.setNumberOfKids(dto.getNumberOfKids());
            singleStudentDto.setMessage(dto.getMessage());

            TransportApplication newApp =
                    service.createApplications(singleStudentDto).get(0);

            results.add(Map.of(
                    "studentUUID", studentUUID,
                    "status", "success",
                    "applicationId", newApp.getApplicationId()
            ));
        }

        // IMPORTANT PART
        if (hasBlocked) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", "One or more students already have an active application",
                    "results", results
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Application submitted successfully",
                "results", results
        ));
    }

    // Get all application by drivers when it approved by admin
    @GetMapping("/{driverUUID}/applications")
    public List<TransportApplicationDTO> getApplications(
            @PathVariable UUID driverUUID) {
        return service.getApplicationsForDriver(driverUUID);
    }

    // Get application by parent
    @GetMapping("/applications/parent/{parentId}/route/{routeId}")
    public ResponseEntity<?> getApplicationsByParentAndRoute(
            @PathVariable UUID parentId,
            @PathVariable UUID routeId) {

        List<TransportApplication> apps = service.getApplicationsByParentAndRoute(parentId, routeId);

        if (apps.isEmpty()) {
            return ResponseEntity.status(404)
                    .body(Map.of("message", "No applications found for this parent and route."));
        }

        return ResponseEntity.ok(apps);
    }

    // Get student by driver uuid
    @GetMapping("/{driverUUID}/students")
    public List<TransportApplicationDTO> getStudents(@PathVariable UUID driverUUID) {
        return service.getApplicationsForDriver(driverUUID);
    }

    // Approved by admin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{driverUUID}/applications/{applicationId}/approve")
    public ResponseEntity<?> approve(@PathVariable UUID applicationId) {
        TransportApplicationDTO dto = service.approveApplication(applicationId);
        return ResponseEntity.ok(dto);
    }

    // Driver received approval notifications
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/{notificationId}/approve")
    public ResponseEntity<Void> approveNotification(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal String email) {

        Driver drivers = driverRepository.findByUserAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found for email: " + email));

        service.driverApprove(notificationId, drivers);

        return ResponseEntity.ok().build();
    }
}
