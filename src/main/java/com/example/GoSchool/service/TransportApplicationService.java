package com.example.GoSchool.service;

import com.example.GoSchool.constant.ApplicationStatus;
import com.example.GoSchool.dtos.TransportApplicationDTO;
import com.example.GoSchool.model.DriverRouteDetails;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.TransportApplication;
import com.example.GoSchool.repository.DriverRouteDetailsRepository;
import com.example.GoSchool.repository.ParentRepository;
import com.example.GoSchool.repository.TransportApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransportApplicationService {

    private final ParentRepository parentRepo;
    private final DriverRouteDetailsRepository routeRepo;
    private final TransportApplicationRepository appRepo;

    public TransportApplicationService(
            ParentRepository parentRepo,
            DriverRouteDetailsRepository routeRepo,
            TransportApplicationRepository appRepo) {

        this.parentRepo = parentRepo;
        this.routeRepo = routeRepo;
        this.appRepo = appRepo;
    }

    public TransportApplication createApplication(TransportApplicationDTO dto) {
        Parent parent = parentRepo.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        DriverRouteDetails route = routeRepo.findById(dto.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        TransportApplication app = new TransportApplication();
        app.setParent(parent);
        app.setRoute(route);
        app.setNumberOfKids(dto.getNumberOfKids());
        app.setMessage(dto.getMessage());
        app.setApplicationStatus(ApplicationStatus.PENDING);
        app.setAppliedAt(LocalDateTime.now());


        return appRepo.save(app);
    }

    public Optional<TransportApplication> getApplicationByParentAndRoute(UUID parentUUID, UUID id) {
        return appRepo.findByParent_ParentUUIDAndRoute_Id(parentUUID, id);
    }

}
