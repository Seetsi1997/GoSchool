package com.example.GoSchool.repository;

import com.example.GoSchool.model.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {
    List<Driver> findByDriverNameIgnoreCase(String driverName);
    List<Driver> findByDriverSurnameIgnoreCase(String driverSurname);
    Optional<Driver> findByUserAccountUuid(UUID userId);

}
