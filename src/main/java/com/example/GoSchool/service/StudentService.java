package com.example.GoSchool.service;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.repository.ParentRepository;
import com.example.GoSchool.repository.PaymentRecordRepository;
import com.example.GoSchool.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final PaymentRecordRepository paymentRecordRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          ParentRepository parentRepository,
                          PaymentRecordRepository paymentRecordRepository) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.paymentRecordRepository = paymentRecordRepository;
    }

    // Create a new student
    public Student createStudent(Student student, UUID parentId, LearnersGrade grade) {
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + parentId));

        student.setStudentGrade(grade); // enum, no need for repository

        // Assign parent
        student.setParent(parent);
        parent.getChildren().add(student);

        return studentRepository.save(student);
    }


    // Get student by UUID
    public Student getStudentById(UUID studentUUID) {
        return studentRepository.findById(studentUUID)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentUUID));
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Update student
    public Student updateStudent(UUID studentUUID, Student updatedStudent) {
        Student existingStudent = getStudentById(studentUUID);
        existingStudent.setStudentFirstName(updatedStudent.getStudentFirstName());
        existingStudent.setStudentSurname(updatedStudent.getStudentSurname());
        existingStudent.setMonthlyPaymentAmount(updatedStudent.getMonthlyPaymentAmount());
        existingStudent.setStudentGrade(updatedStudent.getStudentGrade());
        // Optional: update parents
        existingStudent.setParent(updatedStudent.getParent());

        return studentRepository.save(existingStudent);
    }

    // Delete student
    public void deleteStudent(UUID studentUUID) {
        Student student = getStudentById(studentUUID);
        studentRepository.delete(student);
    }

    // Add a payment record to a student
    public PaymentRecord addPayment(UUID studentUUID, PaymentRecord paymentRecord) {
        Student student = getStudentById(studentUUID);
        paymentRecord.setStudent(student);
        return paymentRecordRepository.save(paymentRecord);
    }

    // Get all payment records for a student
    public List<PaymentRecord> getPaymentsByStudent(UUID studentUUID) {
        Student student = getStudentById(studentUUID);
        return student.getPaymentRecords();
    }
}

