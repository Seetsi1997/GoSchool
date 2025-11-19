package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.PaymentMethod;
import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.model.PaymentRecord;
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

    public PaymentRecordDTO(PaymentRecord paymentRecord){
        this.paymentRecordId = paymentRecord.getPaymentRecordId();
        this.studentId = paymentRecord.getStudent().getStudentUUID();
        this.amount = paymentRecord.getAmount();
        this.paymentDate = paymentRecord.getPaymentDate();
        this.method = paymentRecord.getMethod();
        this.status = paymentRecord.getStatus();
        this.proofOfPaymentUrl = paymentRecord.getProofOfPaymentUrl();
        // Get the admin ID from the Users object
        this.verifiedByAdminId = paymentRecord.getVerifiedByAdmin() != null
                ? paymentRecord.getVerifiedByAdmin().getUuid()
                : null;
    }
}