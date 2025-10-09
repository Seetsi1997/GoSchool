package com.example.GoSchool.service;

import com.example.GoSchool.dtos.LocationDTO;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.model.Location;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.LocationRepository;
import com.example.GoSchool.repository.ParentRepository;
import com.example.GoSchool.repository.StudentRepository;
import com.example.GoSchool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
public class ParentService {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public ParentService(ParentRepository parentRepository,
                         UserRepository userRepository,
                         StudentRepository studentRepository, LocationRepository locationRepository) {
        this.parentRepository = parentRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.locationRepository = locationRepository;
    }

    public void createParent(ParentDTO parentDTO, UUID userId) {
        // Fetch user
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Handle location safely
        Location location;

        if (parentDTO.getLocationUUID() != null) {
            // Existing location
            location = locationRepository.findById(parentDTO.getLocationUUID())
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + parentDTO.getLocationUUID()));
        } else {
            // New location: validate fields
            if (parentDTO.getCity() == null || parentDTO.getCity().isBlank()
                    || parentDTO.getProvince() == null
                    || parentDTO.getAddress() == null || parentDTO.getAddress().isBlank()
                    || parentDTO.getPostalCode() == null || parentDTO.getPostalCode().isBlank()) {
                throw new IllegalArgumentException("Complete location details must be provided");
            }

            Location newLoc = new Location();
            newLoc.setCity(parentDTO.getCity());
            newLoc.setProvince(parentDTO.getProvince());
            newLoc.setAddress(parentDTO.getAddress());
            newLoc.setPostalCode(parentDTO.getPostalCode());
            newLoc.setSuburb(parentDTO.getSuburb());
            location = locationRepository.save(newLoc);
        }

        // Validate required parent fields
        if (parentDTO.getContact() == null || parentDTO.getContact().isBlank()) {
            throw new IllegalArgumentException("Parent contact must be provided");
        }
        if (parentDTO.getFirstName() == null || parentDTO.getFirstName().isBlank()) {
            throw new IllegalArgumentException("Parent first name must be provided");
        }
        if (parentDTO.getSurname() == null || parentDTO.getSurname().isBlank()) {
            throw new IllegalArgumentException("Parent surname must be provided");
        }

        // Create parent entity
        Parent parent = new Parent();
        parent.setParentUUID(parentDTO.getParentUUID());
        parent.setFirstName(parentDTO.getFirstName());
        parent.setSurname(parentDTO.getSurname());
        parent.setParentLocation(location);
        parent.setUserAccount(user);
        parent.setContact(parentDTO.getContact());

        parentRepository.save(parent);
    }

    // Get parent by UUID
    public Parent getParentById(UUID parentUUID) {
        return parentRepository.findById(parentUUID)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + parentUUID));
    }

    // Get all parents
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }


    public Parent getParentByEmail(String email) {
        return parentRepository.findByUserAccountEmail(email)
                .orElseThrow(() -> new RuntimeException("Parent not found with email: " + email));
    }

    public ParentDTO getParentToDTO(Parent parent) {
        ParentDTO dto = new ParentDTO();
        dto.setParentUUID(parent.getParentUUID());
        dto.setFirstName(parent.getFirstName());
        dto.setSurname(parent.getSurname());
        dto.setContact(parent.getContact());

        // Map location info if present
        if (parent.getParentLocation() != null) {
            dto.setLocationUUID(parent.getParentLocation().getLocationUUID());
            dto.setCity(parent.getParentLocation().getCity());
            dto.setAddress(parent.getParentLocation().getAddress());
            dto.setPostalCode(parent.getParentLocation().getPostalCode());
            dto.setProvince(parent.getParentLocation().getProvince());
        }

        // Map user info (email, userId, role) from nested UserAccount
        if (parent.getUserAccount() != null) {
            dto.setEmail(parent.getUserAccount().getEmail());
            dto.setUserId(parent.getUserAccount().getUuid());
            dto.setRole(parent.getUserAccount().getRole());
        }

        // Map children if needed
    /*dto.setStudentDTOList(
        parent.getChildren().stream()
              .map(this::toStudentDTO)
              .collect(Collectors.toList())
    );*/

        return dto;
    }


    // Update parent
    public Parent updateParent(UUID parentUUID, ParentDTO updatedParentDTO) {
        Parent existingParent = getParentById(parentUUID);

        // Update basic fields
        existingParent.setFirstName(updatedParentDTO.getFirstName());
        existingParent.setSurname(updatedParentDTO.getSurname());
        existingParent.setContact(updatedParentDTO.getContact());

        // Keep existing user account
        Users userAccount = existingParent.getUserAccount();
        existingParent.setUserAccount(userAccount);

        // Update location
        Location location = existingParent.getParentLocation();

        if (location != null) {
            location.setCity(updatedParentDTO.getCity());
            location.setAddress(updatedParentDTO.getAddress());
            location.setPostalCode(updatedParentDTO.getPostalCode());
            location.setProvince(updatedParentDTO.getProvince());
        } else {
            // Create new Location if missing
            Location newLocation = new Location();
            newLocation.setCity(updatedParentDTO.getCity());
            newLocation.setAddress(updatedParentDTO.getAddress());
            newLocation.setPostalCode(updatedParentDTO.getPostalCode());
            newLocation.setProvince(updatedParentDTO.getProvince());
            existingParent.setParentLocation(newLocation);
        }

        return parentRepository.save(existingParent);
    }



    // Delete parent
    public void deleteParent(UUID parentUUID) {
        Parent parent = getParentById(parentUUID);
        parentRepository.delete(parent);
    }

    // Optional: find parent by linked user account
    public Parent getParentByUserId(UUID userId) {
        return parentRepository.findByUserAccountUuid(userId)
                .orElseThrow(() -> new RuntimeException("Parent not found for userId: " + userId));
    }

    // Optional: add a child to parent
    public Parent addChildToParent(UUID parentUUID, UUID studentUUID) {
        Parent parent = getParentById(parentUUID);
        Student student = studentRepository.findById(studentUUID)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentUUID));

        parent.getChildren().add(student);
        student.setParent(parent); // make sure Student entity has a parent field
        return parentRepository.save(parent);
    }
}
