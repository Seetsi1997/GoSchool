package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransportApplicationDTO {
    private UUID applicationId;
    private List<UUID> studentUUIDs;
    private UUID parentId;
    private String parentName;
    private String studentName;
    private UUID routeId;
    private String schoolName;
    private String driverName;
    private int numberOfKids;
    private String message;
    private String parentLocation;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}