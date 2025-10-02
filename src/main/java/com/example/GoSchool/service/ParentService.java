package com.example.GoSchool.service;

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

    // Update parent
    public Parent updateParent(UUID parentUUID, Parent updatedParent) {
        Parent existingParent = getParentById(parentUUID);

        existingParent.setFirstName(updatedParent.getFirstName());
        existingParent.setSurname(updatedParent.getSurname());
        existingParent.setContact(updatedParent.getContact());
        existingParent.setParentLocation(updatedParent.getParentLocation());
        // Optional: update children list if needed
        existingParent.setChildren(updatedParent.getChildren());

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
