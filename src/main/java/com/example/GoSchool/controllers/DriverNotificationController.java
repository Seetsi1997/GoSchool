package com.example.GoSchool.controllers;

import com.example.GoSchool.dtos.DriverNotificationDTO;
import com.example.GoSchool.dtos.TransportApplicationDTO;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.repository.DriverRepository;
import com.example.GoSchool.service.DriverNotificationService;
import com.example.GoSchool.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth/api/driver/notifications")
public class DriverNotificationController {

    private final DriverNotificationService notificationService;
    private final DriverService driverService;
    private final DriverRepository driverRepository;

    // Get driver notifications
    @GetMapping
    public List<DriverNotificationDTO> getDriverNotifications(
            Authentication authentication
    ) {
        String email = authentication.getName();
        UUID driverUUID = driverService.getDriverUUIDByEmail(email);
        return notificationService.getNotifications(driverUUID);
    }

    // Mark notification as seen
    @PutMapping("/{notificationId}/seen")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> markAsSeen(
            @PathVariable UUID notificationId
    ) {
        notificationService.markAsSeen(notificationId);
        return ResponseEntity.ok().build();
    }

    // Approve notification by fetching uuid
    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/{notificationId}/approve")
    public ResponseEntity<TransportApplicationDTO> approveNotification(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal String email) {

        Driver drivers = driverRepository.findByUserAccountEmail(email)
                .orElseThrow( () -> new RuntimeException("Driver not found for email: " + email));

     TransportApplicationDTO dto =  notificationService.driverApproveByNotification(notificationId, drivers);

        return ResponseEntity.ok(dto);
    }

    // Reject notification by fetching uuid
    @PutMapping("/result/{id}/reject")
    @PreAuthorize("hasAuthority('DRIVER')")
    public ResponseEntity<TransportApplicationDTO> reject(
            @PathVariable UUID id,
            @AuthenticationPrincipal String email
    ) {

        Driver driver = driverRepository.findByUserAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Driver not found for email: " + email));

        TransportApplicationDTO dto = notificationService.driverRejectByNotification(id, driver);
        return ResponseEntity.ok(dto);

    }

}
