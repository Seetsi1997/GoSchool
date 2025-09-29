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
        UUID locationId = parentDTO.getParentLocation().getLocationUUID();
        if (locationId != null) {
            // Existing location
            location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RuntimeException("Location not found with id: " + locationId));
        } else {
            // New location
            Location newLoc = new Location();
            newLoc.setCity(parentDTO.getParentLocation().getCity());
            newLoc.setProvince(parentDTO.getParentLocation().getProvince());
            newLoc.setAddress(parentDTO.getParentLocation().getAddress());
            newLoc.setPostalCode(parentDTO.getParentLocation().getPostalCode());
            location = locationRepository.save(newLoc);
        }

        // Create parent
        Parent parent = new Parent();
        parent.setParentUUID(parentDTO.getParentUUID());
        parent.setFirstName(parentDTO.getFirstName());
        parent.setSurname(parentDTO.getSurname());
        parent.setContact(parentDTO.getContacts());
        parent.setParentLocation(location);
        parent.setUserAccount(user);

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
