package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.ApplicationStatus;
import com.example.GoSchool.dtos.TransportApplicationDTO;
import com.example.GoSchool.model.TransportApplication;
import com.example.GoSchool.service.TransportApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth/api/transport")
public class TransportApplicationController {

    private final TransportApplicationService service;
    @Autowired
    public TransportApplicationController(TransportApplicationService service) {
        this.service = service;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@RequestBody TransportApplicationDTO dto) {
        // Check if parent already has a PENDING or APPROVED application for this route
        Optional<TransportApplication> existingApp = service.getApplicationByParentAndRoute(dto.getParentId(), dto.getRouteId());

        if (existingApp.isPresent()) {
            TransportApplication app = existingApp.get();
            if (app.getApplicationStatus() == ApplicationStatus.PENDING || app.getApplicationStatus() == ApplicationStatus.APPROVED) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of("message", "You have already applied for this route."));
            }
            // If DECLINED, allow re-application
        }

        TransportApplication newApp = service.createApplication(dto);
        return ResponseEntity.ok(Map.of(
                "message", "Application submitted successfully",
                "applicationId", newApp.getId()
        ));
    }

    @GetMapping("/applications/parent/{parentId}/route/{routeId}")
    public ResponseEntity<?> getApplicationByParentAndRoute(
            @PathVariable UUID parentId,
            @PathVariable UUID routeId) {

        Optional<TransportApplication> app = service.getApplicationByParentAndRoute(parentId, routeId);

        if (app.isPresent()) {
            return ResponseEntity.ok(app.get());
        } else {
            // No application found → return 404
            return ResponseEntity.status(404)
                    .body(Map.of("message", "No application found for this parent and route."));
        }
    }


}
