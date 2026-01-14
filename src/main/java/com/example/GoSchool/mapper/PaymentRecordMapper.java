package com.example.GoSchool.mapper;

import com.example.GoSchool.dtos.PaymentRecordDTO;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.model.Users;
import org.springframework.stereotype.Component;

@Component
public class PaymentRecordMapper {

    // Entity -> DTO
    public PaymentRecordDTO toDTO(PaymentRecord paymentRecord) {
        if (paymentRecord == null) return null;

        PaymentRecordDTO dto = new PaymentRecordDTO();
        dto.setPaymentRecordId(paymentRecord.getPaymentRecordId());
        dto.setAmount(paymentRecord.getAmount());
        dto.setPaymentDate(paymentRecord.getPaymentDate());
        dto.setMethod(paymentRecord.getMethod());
        dto.setStatus(paymentRecord.getStatus());
        dto.setProofOfPaymentUrl(paymentRecord.getProofOfPaymentUrl());

        if (paymentRecord.getStudent() != null) {
            dto.setStudentId(paymentRecord.getStudent().getStudentUUID());
        }

        if (paymentRecord.getVerifiedByAdmin() != null) {
            dto.setVerifiedByAdminId(paymentRecord.getVerifiedByAdmin().getUuid());
        }

        return dto;
    }

    // DTO -> Entity
    public PaymentRecord toEntity(PaymentRecordDTO dto) {
        if (dto == null) return null;

        PaymentRecord paymentRecord = new PaymentRecord();
        paymentRecord.setPaymentRecordId(dto.getPaymentRecordId());
        paymentRecord.setAmount(dto.getAmount());
        paymentRecord.setPaymentDate(dto.getPaymentDate());
        paymentRecord.setMethod(dto.getMethod());
        paymentRecord.setStatus(dto.getStatus());
        paymentRecord.setProofOfPaymentUrl(dto.getProofOfPaymentUrl());

        if (dto.getStudentId() != null) {
            Student student = new Student();
            student.setStudentUUID(dto.getStudentId());
            paymentRecord.setStudent(student);
        }

        if (dto.getVerifiedByAdminId() != null) {
            Users admin = new Users();
            admin.setUuid(dto.getVerifiedByAdminId());
            paymentRecord.setVerifiedByAdmin(admin);
        }

        return paymentRecord;
    }
}
