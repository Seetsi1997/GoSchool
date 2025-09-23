package com.example.GoSchool.repository;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    List<Student> findByParentParentUUID(UUID parentId);
    //List<Student> findByStudentGradeId(UUID gradeId);
    List<Student> findByStudentGrade(LearnersGrade studentGrade);
}
