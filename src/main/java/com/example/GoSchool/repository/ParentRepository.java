package com.example.GoSchool.repository;

import com.example.GoSchool.model.Parent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {
    Optional<Parent> findByUserAccountUuid(UUID userId);
   // List<Parent> findByChildrenStudentUUID(UUID studentId); // find parent by child
   // Change this - the relationship is called "children" not "childrenStudent"
   List<Parent> findByChildren_StudentUUID(UUID studentId);
}
