package com.example.GoSchool.service;

import com.example.GoSchool.dtos.DriverRouteDetailsDTO;
import com.example.GoSchool.model.Driver;
import com.example.GoSchool.model.DriverRouteDetails;
import com.example.GoSchool.repository.DriverRepository;
import com.example.GoSchool.repository.DriverRouteDetailsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class DriverRouteDetailsService {

    private final DriverRouteDetailsRepository routeRepo;
    private final DriverRepository driverRepository;

    public DriverRouteDetailsService(DriverRouteDetailsRepository routeRepo, DriverRepository driverRepository) {
        this.routeRepo = routeRepo;
        this.driverRepository = driverRepository;
    }

    @Transactional
    public DriverRouteDetails createForDriver(UUID driverUUID, DriverRouteDetailsDTO dto) {
        Driver driver = driverRepository.findById(driverUUID)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + driverUUID));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        DriverRouteDetails route = DriverRouteDetails.builder()
                .driver(driver)
                .schoolName(dto.getSchoolName())
                .pickupTime(LocalTime.parse(dto.getPickupTime(), formatter))
                .dropOffTime(LocalTime.parse(dto.getDropOffTime(), formatter))
                .monthlyFee(dto.getMonthlyFee())
                .build();

        DriverRouteDetails saved = routeRepo.save(route);
        // ensure driver has it in collection (optional but convenient)
        driver.getRouteDetails().add(saved);
        driverRepository.save(driver);
        return saved;
    }

    public List<DriverRouteDetails> listForDriver(UUID driverUUID) {
        return routeRepo.findByDriver_DriverUUID(driverUUID);
    }

    public DriverRouteDetails getById(UUID id) {
        return routeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found: " + id));
    }

    @Transactional
    public DriverRouteDetails update(UUID id, DriverRouteDetailsDTO dto) {
        DriverRouteDetails existing = getById(id);
        existing.setSchoolName(dto.getSchoolName());
        existing.setPickupTime(LocalTime.parse(dto.getPickupTime()));
        existing.setDropOffTime(LocalTime.parse(dto.getDropOffTime()));
        existing.setMonthlyFee(dto.getMonthlyFee());
        return routeRepo.save(existing);
    }

    @Transactional
    public void delete(UUID id) {
        routeRepo.deleteById(id);
    }

}
