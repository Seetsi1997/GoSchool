package com.example.GoSchool.repository;

import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentRecord, UUID> {
    List<PaymentRecord> findByStudentStudentUUID(UUID studentId);
    List<PaymentRecord> findByStatus(PaymentStatus status);
}
