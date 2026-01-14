package com.example.GoSchool.controllers;

import com.example.GoSchool.dtos.DriverRouteDetailsDTO;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.DriverRouteDetails;
import com.example.GoSchool.service.DriverRouteDetailsService;
import com.example.GoSchool.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;



@RestController
@RequestMapping("/auth/api/drivers")
public class DriverRouteDetailsController {

    private final DriverRouteDetailsService routeService;
    private final DriverService driverService;

    @Autowired
    public DriverRouteDetailsController(DriverRouteDetailsService routeService, DriverService driverService) {
        this.routeService = routeService;
        this.driverService = driverService;
    }

    // List all routes for a specific driver by driverId
    @GetMapping("/{driverId}/routes")
    public ResponseEntity<List<DriverRouteDetailsDTO>> listRoutesByDriver(@PathVariable UUID driverId) {
        List<DriverRouteDetailsDTO> list = routeService.listForDriver(driverId)
                .stream()
                .map(DriverRouteDetailsDTO::new)
                .toList();
        return ResponseEntity.ok(list);
    }

    // List all routes for the currently logged-in driver
    @GetMapping("/me/routes")
    public ResponseEntity<List<DriverRouteDetailsDTO>> listRoutes(Principal principal) {

        // 1. Get logged-in user's email from JWT
        String email = principal.getName();

        // 2. Load driver by email
        Driver driver = driverService.getDriverByEmail(email);
        if (driver == null) {
            return ResponseEntity.notFound().build();
        }

        // 3. Extract UUID
        UUID driverId = driver.getDriverUUID();

        // 4. Fetch routes for that UUID
        List<DriverRouteDetailsDTO> routes = routeService
                .listForDriver(driverId)
                .stream()
                .map(DriverRouteDetailsDTO::new)
                .toList();

        return ResponseEntity.ok(routes);
    }

    // Get a specific route by routeId for a driver
    @GetMapping("/{driverId}/routes/{routeId}")
    public ResponseEntity<DriverRouteDetailsDTO> getRoute(
            @PathVariable UUID driverId,
            @PathVariable UUID routeId
    ) {
        DriverRouteDetails route = routeService.getById(routeId);
        return ResponseEntity.ok(new DriverRouteDetailsDTO(route));
    }

    // Create route transport by driver uuid
    @PostMapping("/{driverId}/routes")
    public ResponseEntity<?> createRoute(
            @PathVariable UUID driverId,
            @RequestBody DriverRouteDetailsDTO dto
    ) {
        DriverRouteDetails saved = routeService.createForDriver(driverId, dto);
        return ResponseEntity.ok(new DriverRouteDetailsDTO(saved));
    }

    // Update route by driver uuid
    @PutMapping("/{driverId}/routes/{routeId}")
    public ResponseEntity<?> updateRoute(
            @PathVariable UUID driverId,
            @PathVariable UUID routeId,
            @RequestBody DriverRouteDetailsDTO dto
    ) {
        DriverRouteDetails updated = routeService.update(routeId, dto);
        return ResponseEntity.ok(new DriverRouteDetailsDTO(updated));
    }

    // Delete route by driver uuid
    @DeleteMapping("/{driverId}/routes/{routeId}")
    public ResponseEntity<?> deleteRoute(
            @PathVariable UUID driverId,
            @PathVariable UUID routeId
    ) {
        routeService.delete(routeId);
        return ResponseEntity.noContent().build();
    }
}
