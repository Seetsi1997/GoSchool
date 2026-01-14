package com.example.GoSchool.service;


import com.example.GoSchool.constant.Actor;
import com.example.GoSchool.constant.ApplicationStatus;
import com.example.GoSchool.constant.Province;
import com.example.GoSchool.dtos.DriverNotificationDTO;
import com.example.GoSchool.dtos.TransportApplicationDTO;
import com.example.GoSchool.model.*;
import com.example.GoSchool.repository.DriverNotificationRepository;
import com.example.GoSchool.repository.DriverRepository;
import com.example.GoSchool.repository.StudentRepository;
import com.example.GoSchool.repository.TransportApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverNotificationService {

    private final DriverNotificationRepository notificationRepo;
    private final TransportApplicationRepository appRepo;
    private final StudentRepository studentRepo;

    /**
     * Driver notify driver application
     */
    public void notifyDriver(
            TransportApplication application,
            String message
    ) {
        Driver driver = application.getRoute().getDriver();

        DriverNotification notification = new DriverNotification();
        notification.setDriver(driver);
        notification.setApplication(application);
        notification.setMessage(message);

        notificationRepo.save(notification);
    }

    /**
     * Driver get notifications for application
     */
    public List<DriverNotificationDTO> getNotifications(UUID driverUUID) {
        return notificationRepo
                .findByDriverDriverUUIDOrderByCreatedAtDesc(driverUUID)
                .stream()
                .map(n -> {
                    TransportApplication app = n.getApplication();

                    // Combine all student names into a single string
                    String studentNames = app.getStudent() != null
                            ? app.getStudent().getStudentFirstName() + " " + app.getStudent().getStudentSurname()
                            : "";
                    String address = app.getParent().getParentLocation() != null
                            ? app.getParent().getParentLocation().getAddress()
                            : "";
                    String city = app.getParent().getParentLocation() != null
                            ? app.getParent().getParentLocation().getCity()
                            : "";
                    String suburb = app.getParent().getParentLocation() != null
                            ? app.getParent().getParentLocation().getSuburb()
                            : "";
                    String province = app.getParent().getParentLocation() != null
                            ? app.getParent().getParentLocation().getProvince().name()
                            : "";
                    String postalCode = app.getParent().getParentLocation() != null
                            ? app.getParent().getParentLocation().getPostalCode()
                            : "";
                    String schoolNames = app.getRoute() != null
                            ? app.getRoute().getSchoolName()
                            : "";

                    return new DriverNotificationDTO(
                            n.getId(),
                            n.getMessage(),
                            n.isSeen(),
                            n.getCreatedAt(),
                            app.getApplicationId(),
                            n.getDriver().getDriverUUID(),
                            app.getApplicationStatus(),
                            n.getActor(),
                            app.getParent().getFirstName() + " " + app.getParent().getSurname(),
                            studentNames,
                            address,
                            suburb,
                            city,
                            province,
                            postalCode,
                            schoolNames
                    );

                })
                .toList();
    }

    /**
     * Driver miss mark as seen which means approved an application
     */
    public void markAsSeen(UUID notificationId) {
        DriverNotification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.setSeen(true);
        notificationRepo.save(notification);
    }

    /**
     * Driver approves an application
     */
    @Transactional
    public TransportApplicationDTO driverApproveByNotification(UUID applicationId, Driver driver) {

        TransportApplication app = appRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getRoute().getDriver().getDriverUUID().equals(driver.getDriverUUID())) {
            throw new RuntimeException("Driver mismatch!");
        }

        // Approve application
        app.setApplicationStatus(ApplicationStatus.APPROVED_BY_DRIVER);
        appRepo.save(app);

        // Mark all previous notifications as seen
        List<DriverNotification> notifications =
                notificationRepo.findByApplicationApplicationId(applicationId);
        notifications.forEach(n -> n.setSeen(true));
        notificationRepo.saveAll(notifications);

        // Create confirmation notification
        DriverNotification driverNotification = new DriverNotification();
        driverNotification.setDriver(driver);
        driverNotification.setApplication(app);
        driverNotification.setActor(Actor.DRIVER);
        driverNotification.setMessage("You approved this application");

        driverNotification.setApplicationStatus(ApplicationStatus.APPROVED_BY_DRIVER);


        notificationRepo.save(driverNotification);

        // Build DTO for frontend (with student & parent info)
        return buildTransportApplicationDTO(app);
    }

    private TransportApplicationDTO buildTransportApplicationDTO(TransportApplication app) {
        TransportApplicationDTO dto = new TransportApplicationDTO();
        dto.setApplicationId(app.getApplicationId());
        dto.setStudentUUIDs(app.getStudent() != null
                ? List.of(app.getStudent().getStudentUUID())
                : Collections.emptyList());
        dto.setStudentName(app.getStudent() != null
                ? app.getStudent().getStudentFirstName() + " " + app.getStudent().getStudentSurname()
                : "");
        dto.setParentId(app.getParent().getParentUUID());
        dto.setParentName(app.getParent().getFirstName() + " " + app.getParent().getSurname());
        dto.setParentLocation(app.getParent().getParentLocation().getAddress() + ", "
                + app.getParent().getParentLocation().getSuburb()
                + app.getParent().getParentLocation().getCity()
                + app.getParent().getParentLocation().getProvince()
                + app.getParent().getParentLocation().getPostalCode());
        dto.setRouteId(app.getRoute().getId());
        dto.setDriverName(app.getRoute().getDriver().getDriverName());
        dto.setSchoolName(app.getRoute().getSchoolName());
        dto.setNumberOfKids(app.getNumberOfKids());
        dto.setMessage(app.getMessage());
        dto.setStatus(app.getApplicationStatus());
        dto.setAppliedAt(app.getAppliedAt());
        return dto;
    }

    /**
     * Driver reject an application
     */
    @Transactional
    public TransportApplicationDTO driverRejectByNotification(UUID applicationId, Driver driver) {

        TransportApplication app = appRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!app.getRoute().getDriver().getDriverUUID().equals(driver.getDriverUUID())) {
            throw new RuntimeException("Driver mismatch!");
        }

        // Decline application and deactivate
        app.setApplicationStatus(ApplicationStatus.DECLINED);
        app.setActive(false);
        appRepo.save(app);

        // Mark previous notifications as seen
        List<DriverNotification> notifications =
                notificationRepo.findByApplicationApplicationId(applicationId);
        notifications.forEach(n -> n.setSeen(true));
        notificationRepo.saveAll(notifications);

        // Create rejection notification
        DriverNotification notification = new DriverNotification();
        notification.setDriver(driver);
        notification.setApplication(app);
        notification.setActor(Actor.DRIVER);
        notification.setMessage("You rejected this application");
        notification.setApplicationStatus(ApplicationStatus.DECLINED);
        notificationRepo.save(notification);

        // Return DTO with student & parent info
        return buildTransportApplicationDTO(app);
    }

}
