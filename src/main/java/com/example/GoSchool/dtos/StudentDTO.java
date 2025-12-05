package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.constant.Province;
import com.example.GoSchool.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private UUID studentUUID;
    private String studentFirstName;
    private String studentSurname;
    private String schoolName;
    private double monthlyPaymentAmount;
    private PaymentStatus paymentStatus;
    private LearnersGrade studentGrade;
    private UUID parentUUID;
    private String parentName;
    private String parentPhoneNumber;
    private String parentEmail;
    private String parentAddress;
    private String parentCity;
    private String parentPostalCode;
    private Province parentProvince;
    private List<PaymentRecordDTO> paymentRecordDTOS;
    private DriverDTO driverDto;

    public StudentDTO(Student student) {
        if(student == null) return;

        this.studentUUID = student.getStudentUUID();
        this.studentFirstName = student.getStudentFirstName();
        this.studentSurname = student.getStudentSurname();
        this.schoolName = student.getSchoolName();
        this.monthlyPaymentAmount = student.getMonthlyPaymentAmount();
        this.paymentStatus = student.getPaymentStatus();
        this.studentGrade = student.getStudentGrade();

        if(student.getParent() != null) {
            this.parentUUID = student.getParent().getParentUUID();
            this.parentName = student.getParent().getFirstName();
            this.parentPhoneNumber = student.getParent().getContact();
            this.parentEmail = student.getParent().getUserAccount().getEmail();
            this.parentAddress = student.getParent().getParentLocation().getAddress() + ", " +
                    student.getParent().getParentLocation().getCity() + ", " +
                    student.getParent().getParentLocation().getProvince() + ", " +
                    student.getParent().getParentLocation().getPostalCode();
            this.parentCity = student.getParent().getParentLocation().getCity();
            this.parentPostalCode = student.getParent().getParentLocation().getPostalCode();
            this.parentProvince = student.getParent().getParentLocation().getProvince();
        }

        this.paymentRecordDTOS = student.getPaymentRecords() != null
                ? student.getPaymentRecords().stream()
                .map(PaymentRecordDTO::new)
                .collect(Collectors.toList())
                : new ArrayList<>();

        if(student.getDriver() != null) {
            this.driverDto = new DriverDTO(student.getDriver(), false);
        }


    }

    public Student toEntity() {
        Student s = new Student();
        s.setStudentUUID(this.studentUUID);
        return s;
    }

}
