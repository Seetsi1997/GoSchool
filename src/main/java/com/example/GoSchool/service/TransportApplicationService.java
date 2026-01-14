package com.example.GoSchool.service;

import com.example.GoSchool.constant.Actor;
import com.example.GoSchool.constant.ApplicationStatus;
import com.example.GoSchool.dtos.TransportApplicationDTO;
import com.example.GoSchool.model.*;
import com.example.GoSchool.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransportApplicationService {

    private static final Logger log = LoggerFactory.getLogger(TransportApplicationService.class);
    private final ParentRepository parentRepo;
    private final DriverRouteDetailsRepository routeRepo;
    private final TransportApplicationRepository appRepo;
    private final DriverNotificationRepository notificationRepo;
    private final StudentRepository studentRepository;

    @Autowired
    public TransportApplicationService(
            ParentRepository parentRepo,
            DriverRouteDetailsRepository routeRepo,
            TransportApplicationRepository appRepo,
            DriverNotificationRepository notificationRepo,
            StudentRepository studentRepository) {

        this.parentRepo = parentRepo;
        this.routeRepo = routeRepo;
        this.appRepo = appRepo;
        this.notificationRepo = notificationRepo;
        this.studentRepository = studentRepository;
    }

    /** Users Apply for transport **/
    public List<TransportApplication> createApplications(TransportApplicationDTO dto) {

        Parent parent = parentRepo.findById(dto.getParentId())
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        DriverRouteDetails route = routeRepo.findById(dto.getRouteId())
                .orElseThrow(() -> new RuntimeException("Route not found"));

        if (dto.getStudentUUIDs() == null || dto.getStudentUUIDs().isEmpty()) {
            throw new RuntimeException("No students provided");
        }

        List<TransportApplication> createdApplications = new ArrayList<>();

        for (UUID studentUUID : dto.getStudentUUIDs()) {

            Student student = studentRepository.findById(studentUUID)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentUUID));

            boolean exists = appRepo
                    .existsByParent_ParentUUIDAndStudent_StudentUUIDAndRoute_IdAndActiveTrue(
                            parent.getParentUUID(),
                            studentUUID,
                            route.getId()
                    );

            if (exists) {
                throw new RuntimeException(
                        "Active transport application already exists for this student"
                );
            }

            TransportApplication app = new TransportApplication();
            app.setParent(parent);
            app.setStudent(student);
            app.setRoute(route);
            app.setNumberOfKids(dto.getNumberOfKids());
            app.setMessage(dto.getMessage());
            app.setApplicationStatus(ApplicationStatus.PENDING);
            app.setActive(true);
            app.setAppliedAt(LocalDateTime.now());

            createdApplications.add(appRepo.save(app));
        }

        return createdApplications;
    }

    /** Users get application to their parents and router were they applied to **/
    public List<TransportApplication> getApplicationsByParentAndRoute(UUID parentUUID, UUID routeId) {
        return appRepo.findByParent_ParentUUIDAndRoute_Id(parentUUID, routeId);
    }

    /** Users get application for drivers **/
    public List<TransportApplicationDTO> getApplicationsForDriver(UUID driverUUID) {
        List<TransportApplication> apps = appRepo.findByRoute_Driver_DriverUUID(driverUUID);

        return apps.stream().map(app -> getTransportApplicationDTO(app, app.getRoute().getDriver())).collect(Collectors.toList());
    }

    /** Users approving application by the admin **/
    @Transactional
    public TransportApplicationDTO approveApplication(UUID applicationId) {

        // 1 Fetch the transport application
        TransportApplication app = appRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        //  Update the status to approved by admin
        app.setApplicationStatus(ApplicationStatus.APPROVED_BY_ADMIN);
        appRepo.save(app);

        // Create a notification for the driver
        Driver driver = app.getRoute().getDriver();

        DriverNotification notification = new DriverNotification();
        notification.setDriver(driver);
        notification.setActor(Actor.ADMIN);
        notification.setApplication(app);
        notification.setMessage("A transport application for your route was approved by admin");
        notification.setApplicationStatus(app.getApplicationStatus());

        notificationRepo.save(notification);



        // Return DTO to frontend
        return getTransportApplicationDTO(app, driver);
    }

    // Build DTO to return to frontend
    private static TransportApplicationDTO getTransportApplicationDTO(TransportApplication app, Driver driver) {
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
        dto.setSchoolName(app.getRoute().getSchoolName());
        dto.setDriverName(driver.getDriverName());
        dto.setNumberOfKids(app.getNumberOfKids());
        dto.setMessage(app.getMessage());
        dto.setStatus(app.getApplicationStatus());
        dto.setAppliedAt(app.getAppliedAt());
        return dto;
    }


    /** Users delete application **/
    public void deleteApplication(UUID applicationId) {
        TransportApplication app = appRepo.findById(applicationId)
                .orElseThrow();
        app.setActive(false);
        appRepo.save(app);
    }

    /** Driver users approving application for the transport **/
   @Transactional
    public void driverApprove(UUID applicationId, Driver driver) {

        TransportApplication app = appRepo.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        Driver routeDriver = app.getRoute().getDriver();

        // SAFETY CHECK - compare UUIDs
        if (!routeDriver.getDriverUUID().equals(driver.getDriverUUID())) {
            throw new RuntimeException(
                    "Driver mismatch. Route driver: " + routeDriver.getDriverUUID()
                            + ", Request driver: " + driver.getDriverUUID()
            );
        }

        // Approve application
        app.setApplicationStatus(ApplicationStatus.APPROVED_BY_DRIVER);
        appRepo.save(app);

        // Assign driver to student
        Student student = studentRepository.findById(
                app.getStudent().getStudentUUID()
        ).orElseThrow(() -> new RuntimeException("Student not found"));

        student.setDriver(driver);
        student.setTransportApplicationStatus(ApplicationStatus.APPROVED_BY_DRIVER);
        studentRepository.save(student);

        // Keep bidirectional sync
        if (driver.getAssignedStudents() == null) {
            driver.setAssignedStudents(new ArrayList<>());
        }

        // Prevent duplicates
        boolean alreadyAssigned = driver.getAssignedStudents().stream()
                .anyMatch(s -> s.getStudentUUID().equals(student.getStudentUUID()));

        if (!alreadyAssigned) {
            driver.getAssignedStudents().add(student);
        }

        // MARK NOTIFICATION AS SEEN - HANDLE MULTIPLE RESULTS
        List<DriverNotification> notifications = notificationRepo.findByApplicationApplicationId(applicationId);

        if (!notifications.isEmpty()) {
            notifications.forEach(notification -> {
                notification.setSeen(true);
                log.info("Marking notification as seen: {}", notification.getId());
            });
            notificationRepo.saveAll(notifications);
        } else {
            log.warn("No notifications found for application: {}", applicationId);
        }

    }

}
