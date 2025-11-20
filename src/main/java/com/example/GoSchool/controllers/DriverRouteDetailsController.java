package com.example.GoSchool.controllers;

import com.example.GoSchool.dtos.DriverRouteDetailsDTO;
import com.example.GoSchool.model.DriverRouteDetails;
import com.example.GoSchool.service.DriverRouteDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/api/drivers/{driverId}/routes")
public class DriverRouteDetailsController {

    private final DriverRouteDetailsService routeService;

    public DriverRouteDetailsController(DriverRouteDetailsService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<?> createRoute(@PathVariable UUID driverId, @RequestBody DriverRouteDetailsDTO dto) {
        DriverRouteDetails saved = routeService.createForDriver(driverId, dto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<DriverRouteDetails>> listRoutes(@PathVariable UUID driverId) {
        List<DriverRouteDetails> list = routeService.listForDriver(driverId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<DriverRouteDetails> getRoute(@PathVariable UUID driverId, @PathVariable UUID routeId) {
        // Optionally check driverId matches route.driver
        DriverRouteDetails r = routeService.getById(routeId);
        return ResponseEntity.ok(r);
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<?> updateRoute(@PathVariable UUID driverId, @PathVariable UUID routeId,
                                         @RequestBody DriverRouteDetailsDTO dto) {
        DriverRouteDetails updated = routeService.update(routeId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable UUID driverId, @PathVariable UUID routeId) {
        routeService.delete(routeId);
        return ResponseEntity.noContent().build();
    }
}
