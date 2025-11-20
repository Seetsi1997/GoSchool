package com.example.GoSchool.repository;

import com.example.GoSchool.model.DriverRouteDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DriverRouteDetailsRepository extends JpaRepository<DriverRouteDetails, UUID> {
    List<DriverRouteDetails> findByDriver_DriverUUID(UUID driverUUID);
}
