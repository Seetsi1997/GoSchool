package com.example.GoSchool.repository;

import com.example.GoSchool.model.TransportApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransportApplicationRepository extends JpaRepository<TransportApplication, UUID> {
    //Optional<TransportApplication> findByParent_ParentUUIDAndRoute_Id(UUID parentUUID, UUID id);
    List<TransportApplication> findByRoute_Driver_DriverUUID(UUID driverUUID);
    List<TransportApplication> findByParent_ParentUUIDAndRoute_Id(UUID parentUUID, UUID id);
    boolean existsByParent_ParentUUIDAndStudent_StudentUUIDAndRoute_IdAndActiveTrue(
            UUID parentId, UUID studentId, UUID routeId
    );

}


