package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.PaymentMethod;
import com.example.GoSchool.constant.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecordDTO {
    private UUID paymentRecordId;
    private UUID studentId;
    private double amount;
    private LocalDate paymentDate;
    private PaymentMethod method;
    private PaymentStatus status;
    private String proofOfPaymentUrl;
    private UUID verifiedByAdminId;
}
