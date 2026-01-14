package com.example.GoSchool.service;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.dtos.StudentDTO;
import com.example.GoSchool.mapper.StudentMapper;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.repository.ParentRepository;
import com.example.GoSchool.repository.PaymentRecordRepository;
import com.example.GoSchool.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private static final Logger log = LoggerFactory.getLogger(StudentService.class);
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final PaymentRecordRepository paymentRecordRepository;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          ParentRepository parentRepository,
                          PaymentRecordRepository paymentRecordRepository,
                          StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
        this.paymentRecordRepository = paymentRecordRepository;
        this.studentMapper = studentMapper;
    }

    // Create a new student
    @Transactional
    public Student createStudent(Student student, UUID parentId, LearnersGrade grade) {
        // 1 Fetch the parent with its children and location eagerly
        Parent parent = parentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found with id: " + parentId));

        // 2 Set student grade and parent
        student.setStudentGrade(grade);
        student.setParent(parent);


        // 3 Add student to parent's children list
        parent.getChildren().add(student);

        // 4 Save the student
        Student savedStudent = studentRepository.save(student);

        // 5 Optional: reload saved student with parent eagerly if lazy-loading is enabled
        return studentRepository.findById(savedStudent.getStudentUUID())
                .orElseThrow(() -> new RuntimeException("Saved student not found"));
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
        existingStudent.setSchoolName(updatedStudent.getSchoolName());
        existingStudent.setMonthlyPaymentAmount(updatedStudent.getMonthlyPaymentAmount());
        existingStudent.setStudentGrade(updatedStudent.getStudentGrade());

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

    // Get all students for a specific parent
    public List<Student> getStudentsByParentId(UUID parentId) {
        // Verify parent exists first
        if (!parentRepository.existsById(parentId)) {
            throw new RuntimeException("Parent not found with id: " + parentId);
        }
        return studentRepository.findByParent_ParentUUID(parentId);
    }

    // Get all students for a specific parent with DTO conversion
    public List<StudentDTO> getStudentsByParentIdAsDTO(UUID parentId) {
        // Verify parent exists first
        if (!parentRepository.existsById(parentId)) {
            throw new RuntimeException("Parent not found with id: " + parentId);
        }

        List<Student> students = studentRepository.findByParent_ParentUUID(parentId);
        return students.stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }

    // Get student by ID with parent verification
    public Student getStudentByIdAndParentId(UUID studentUUID, UUID parentId) {
        Student student = studentRepository.findById(studentUUID)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentUUID));

        // Verify the student belongs to the specified parent
        if (student.getParent() == null || !student.getParent().getParentUUID().equals(parentId)) {
            throw new RuntimeException("Student does not belong to the specified parent");
        }

        return student;
    }

    // Get student by ID with parent verification and return as DTO
    public StudentDTO getStudentByIdAndParentIdAsDTO(UUID studentUUID, UUID parentId) {
        Student student = getStudentByIdAndParentId(studentUUID, parentId);
        return new StudentDTO(student);
    }

    // Get students by grade
    public List<Student> getStudentsByGrade(LearnersGrade studentGrade) {
        return studentRepository.findByStudentGrade(studentGrade);
    }

    // Get students by grade with DTO conversion
    public List<StudentDTO> getStudentsByGradeAsDTO(LearnersGrade studentGrade) {
        List<Student> students = studentRepository.findByStudentGrade(studentGrade);
        return students.stream()
                .map(StudentDTO::new)
                .collect(Collectors.toList());
    }

    // Check if student belongs to parent
    public boolean doesStudentBelongToParent(UUID studentUUID, UUID parentId) {
        try {
            Student student = getStudentById(studentUUID);
            return student.getParent() != null && student.getParent().getParentUUID().equals(parentId);
        } catch (RuntimeException e) {
            return false;
        }
    }

    // Get students count by parent
    public long getStudentCountByParentId(UUID parentId) {
        if (!parentRepository.existsById(parentId)) {
            throw new RuntimeException("Parent not found with id: " + parentId);
        }
        return studentRepository.findByParent_ParentUUID(parentId).size();
    }

    // Get student by their driver
    public List<StudentDTO> getStudentsByDriver(UUID driverUUID) {
        return studentRepository
                .findByDriver_DriverUUID(driverUUID)
                .stream()
                .map(studentMapper::toDTO)
                .toList();
    }

    // Get students by their parent
    public List<StudentDTO> getStudentsByParent(UUID parentUUID) {
        List<StudentDTO> students;
        students = studentRepository.findByParent_ParentUUID(parentUUID)
                .stream()
                .map(StudentDTO::new)
                .toList();

        return students;
    }

}

