package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.model.PaymentRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {
    private UUID studentUUID;
    private String studentFirstName;
    private String studentSurname;
    private double monthlyPaymentAmount;
    private PaymentStatus paymentStatus;
    private LearnersGrade studentGrade;
    private List<ParentDTO> parentDTOS;
    private List<PaymentRecordDTO> paymentRecordDTOS;
}
