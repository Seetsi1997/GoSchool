package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.constant.Province;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public StudentDTO(Student student) {
        this.studentUUID = student.getStudentUUID();
        this.studentFirstName = student.getStudentFirstName();
        this.studentSurname = student.getStudentSurname();
        this.schoolName = student.getSchoolName();
        this.monthlyPaymentAmount = student.getMonthlyPaymentAmount();
        this.paymentStatus = student.getPaymentStatus();
        this.studentGrade = student.getStudentGrade();
        this.parentUUID = student.getParent().getParentUUID();
        this.parentName = student.getParent() != null
                ? student.getParent().getFirstName()
                : null;
        this.parentAddress = student.getParent() != null
                ? student.getParent().getParentLocation().getAddress()
                + ", " + student.getParent().getParentLocation().getCity()
                + ", " + student.getParent().getParentLocation().getProvince()
                + ", " + student.getParent().getParentLocation().getPostalCode()
                : null;
        this.parentPhoneNumber = student.getParent() != null
                ? student.getParent().getContact()
                : null;
        this.parentEmail = student.getParent() != null
                ? student.getParent().getUserAccount().getEmail()
                : null;

        this.paymentRecordDTOS = student.getPaymentRecords() != null
                ? student.getPaymentRecords().stream()
                .map(PaymentRecordDTO::new)
                .collect(Collectors.toList())
                : null;
    }

}