package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.PaymentMethod;
import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.model.Users;
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

    public PaymentRecord toEntity() {
        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentRecordId(this.paymentRecordId);
        paymentRecord.setAmount(this.amount);
        paymentRecord.setPaymentDate(this.paymentDate);
        paymentRecord.setMethod(this.method);
        paymentRecord.setStatus(this.status);
        paymentRecord.setProofOfPaymentUrl(this.proofOfPaymentUrl);

        if (this.studentId != null) {
            Student student = new Student();
            student.setStudentUUID(this.studentId);
            paymentRecord.setStudent(student);
        }

        if (this.verifiedByAdminId != null) {
            Users admin = new Users();
            admin.setUuid(this.verifiedByAdminId);
            paymentRecord.setVerifiedByAdmin(admin);
        }

        return paymentRecord;
    }

}