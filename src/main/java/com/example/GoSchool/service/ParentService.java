package com.example.GoSchool.service;

import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.model.Users;
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

    @Autowired
    public ParentService(ParentRepository parentRepository,
                         UserRepository userRepository,
                         StudentRepository studentRepository) {
        this.parentRepository = parentRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    // Create a new Parent
    public Parent createParent(Parent parent, UUID userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        parent.setUserAccount(user);
        // Children list can be empty initially or populated later
        return parentRepository.save(parent);
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
