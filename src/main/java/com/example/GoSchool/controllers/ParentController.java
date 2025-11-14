package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.constant.Role;
import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.dtos.StudentDTO;
import com.example.GoSchool.model.Parent;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.ParentRepository;
import com.example.GoSchool.repository.StudentRepository;
import com.example.GoSchool.service.AuthService;
import com.example.GoSchool.service.ParentService;
import com.example.GoSchool.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/api/parents")
public class ParentController {

    private final AuthService authService;
    private final ParentService parentService;
    private final StudentService studentService;
    private final StudentRepository studentRepository;
    private  final ParentRepository parentRepository;
    //private final PasswordEncoder passwordEncoder;

    public ParentController (ParentService parentService, AuthService authService, PasswordEncoder passwordEncoder,
                             StudentService studentService,  StudentRepository studentRepository,
                             ParentRepository parentRepository){
        this.parentService = parentService;
        this.authService = authService;
        this.studentService = studentService;
        this.studentRepository = studentRepository;
        this.parentRepository = parentRepository;
       // this.passwordEncoder = passwordEncoder;
    }

    private Student mapToEntity(StudentDTO dto) {
        Student student = new Student();
        student.setStudentUUID(dto.getStudentUUID());
        student.setStudentFirstName(dto.getStudentFirstName());
        student.setStudentSurname(dto.getStudentSurname());
        student.setSchoolName(dto.getSchoolName());
        student.setMonthlyPaymentAmount(dto.getMonthlyPaymentAmount());
        student.setPaymentStatus(dto.getPaymentStatus());
        student.setStudentGrade(dto.getStudentGrade());
        return student;
    }

    private StudentDTO mapToDTO(Student student) {
        StudentDTO dto = new StudentDTO();
        dto.setStudentUUID(student.getStudentUUID());
        dto.setStudentFirstName(student.getStudentFirstName());
        dto.setStudentSurname(student.getStudentSurname());
        dto.setSchoolName(student.getSchoolName());
        dto.setMonthlyPaymentAmount(student.getMonthlyPaymentAmount());
        dto.setPaymentStatus(student.getPaymentStatus());
        dto.setStudentGrade(student.getStudentGrade());


        if (student.getParent() != null) {
            ParentDTO parentDTO = getParentDTO(student);
            dto.setParentName(parentDTO.getFirstName());
            dto.setParentAddress(parentDTO.getAddress());
            dto.setParentEmail(parentDTO.getEmail());
            dto.setParentPhoneNumber(parentDTO.getContact());
            dto.setParentCity(parentDTO.getCity());
            dto.setParentPostalCode(parentDTO.getPostalCode());
            dto.setParentProvince(parentDTO.getProvince());
        }

        return dto;
    }

    private static ParentDTO getParentDTO(Student student) {
        Parent parent = student.getParent();
        ParentDTO parentDTO = new ParentDTO();
        parentDTO.setParentUUID(parent.getParentUUID());
        parentDTO.setFirstName(parent.getFirstName());
        parentDTO.setSurname(parent.getSurname());
        parentDTO.setEmail(parent.getUserAccount().getEmail());
        parentDTO.setUserId(student.getStudentUUID());
        parentDTO.setContact(parent.getContact());
        parentDTO.setRole(parent.getUserAccount().getRole());

        if (parent.getParentLocation() != null) {
            parentDTO.setLocationUUID(parent.getParentLocation().getLocationUUID());
            parentDTO.setAddress(parent.getParentLocation().getAddress());
            parentDTO.setCity(parent.getParentLocation().getCity());
            parentDTO.setPostalCode(parent.getParentLocation().getPostalCode());
            parentDTO.setSuburb(parent.getParentLocation().getSuburb());
            parentDTO.setProvince(parent.getParentLocation().getProvince());
        }

        // Map each child Student entity to StudentDTO
        List<StudentDTO> studentDTOs = parent.getChildren()
                .stream()
                .map(child -> {
                    StudentDTO dto = new StudentDTO();
                    dto.setStudentUUID(child.getStudentUUID());
                    dto.setStudentFirstName(child.getStudentFirstName());
                    dto.setStudentSurname(child.getStudentSurname());
                    dto.setSchoolName(child.getSchoolName());
                    dto.setMonthlyPaymentAmount(child.getMonthlyPaymentAmount());
                    dto.setPaymentStatus(child.getPaymentStatus());
                    dto.setStudentGrade(child.getStudentGrade());
                    dto.setParentName(child.getParent().getFirstName());
                    return dto;
                })
                .toList();

        parentDTO.setStudentDTOList(studentDTOs);

        return parentDTO;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerParent(@Valid @RequestBody ParentDTO parentDTO) {
        try {
            Users savedUser = authService.registerBaseUser(
                    parentDTO.getEmail(),
                    parentDTO.getFirstName() + " " + parentDTO.getSurname(),
                    Role.PARENT,
                    parentDTO.getPassword(),
                    parentDTO.getContact()
            );

            parentService.createParent(parentDTO, savedUser.getUuid()); // now UUID exists

            return ResponseEntity.ok(Map.of("message", "Parent registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get current logged-in parent to get parent profile
    @GetMapping("/profile")
    public ResponseEntity<ParentDTO> getCurrentParentProfile(Authentication authentication) {
        String email = authentication.getName();

        // Fetch parent entity by email
        Parent parent = parentService.getParentByEmail(email);

        // Convert to DTO
        ParentDTO parentDTO = parentService.getParentToDTO(parent);

        return ResponseEntity.ok(parentDTO);
    }

    // Update current parent profile
    @PutMapping("/profile")
    public ResponseEntity<ParentDTO> updateCurrentParentProfile(
            Authentication authentication,
            @RequestBody ParentDTO updatedParentDTO) {

        String email = authentication.getName();

        // Fetch parent entity
        Parent existingParent = parentService.getParentByEmail(email);

        // Update parent with DTO
        Parent updatedParent = parentService.updateParent(existingParent.getParentUUID(), updatedParentDTO);

        // Convert updated entity to DTO to return
        ParentDTO updatedParentDTOResponse = parentService.getParentToDTO(updatedParent);

        return ResponseEntity.ok(updatedParentDTOResponse);
    }

    @PostMapping("/{parentId}/students")
    public ResponseEntity<StudentDTO> createStudent(
            @PathVariable UUID parentId,
            @RequestBody StudentDTO studentDTO
    ) {
        LearnersGrade grade = studentDTO.getStudentGrade();
        Student student = mapToEntity(studentDTO);

        // save and reload
        Student savedStudent = studentService.createStudent(student, parentId, grade);
        Student fullStudent = studentRepository.findById(savedStudent.getStudentUUID())
                .orElseThrow(() -> new RuntimeException("Saved student not found"));

        StudentDTO response = mapToDTO(fullStudent);
        return ResponseEntity.ok(response);
    }

    // Get all students for current parent
    @GetMapping("/me/students")
    public ResponseEntity<?> getMyStudents(Authentication authentication) {
        try {
            String email = authentication.getName();
            Parent parent = parentRepository.findByUserAccountEmail(email)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));

            List<StudentDTO> students = studentService.getStudentsByParentIdAsDTO(parent.getParentUUID());
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    // Get specific student belonging to current parent
    @GetMapping("/me/students/{studentUUID}")
    public ResponseEntity<StudentDTO> getMyStudent(
            @RequestHeader("X-Parent-ID") UUID parentId,
            @PathVariable UUID studentUUID) {
        try {
            Student student = studentService.getStudentByIdAndParentId(studentUUID, parentId);
            return ResponseEntity.ok(new StudentDTO(student));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get students by grade
    @GetMapping("/students/grade/{grade}")
    public ResponseEntity<List<StudentDTO>> getStudentsByGrade(
            @PathVariable LearnersGrade grade) {
        try {
            List<Student> students = studentService.getStudentsByGrade(grade);
            List<StudentDTO> studentDTOs = students.stream()
                    .map(StudentDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(studentDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{parentUUID}/students/{studentId}")
    public ResponseEntity<?> updateStudent(
            @PathVariable UUID parentId,
            @PathVariable UUID studentId,
            @Valid @RequestBody StudentDTO studentUpdateDTO) {

        try {
            // Find student
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            // Check if student belongs to parent
            if (!isStudentBelongsToParent(student, parentId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Student does not belong to this parent");
            }

            // Update student
            student.setSchoolName(studentUpdateDTO.getSchoolName());
            student.setStudentGrade(studentUpdateDTO.getStudentGrade());

            Student updatedStudent = studentRepository.save(student);
            return ResponseEntity.ok(updatedStudent);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Helper method to check ownership
    private boolean isStudentBelongsToParent(Student student, UUID parentId) {
        // Check if student has a parent and if the parent ID matches
        return student.getParent() != null &&
                student.getParent().getParentUUID().equals(parentId);
    }

}
