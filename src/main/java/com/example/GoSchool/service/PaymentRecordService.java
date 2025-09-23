package com.example.GoSchool.service;

import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.dtos.PaymentRecordDTO;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.model.Users;
import com.example.GoSchool.repository.PaymentRecordRepository;
import com.example.GoSchool.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentRecordService {

    private final PaymentRecordRepository paymentRecordRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public PaymentRecordService(PaymentRecordRepository paymentRecordRepository,
                                StudentRepository studentRepository) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.studentRepository = studentRepository;
    }

    // Create a new payment record for a student
    public PaymentRecord createPayment(UUID studentId, PaymentRecord paymentRecord) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        paymentRecord.setStudent(student); // set the student reference
        paymentRecord.setPaymentDate(LocalDate.now()); // set payment date to today

        // Optional: set default status if not set
        if (paymentRecord.getStatus() == null) {
            paymentRecord.setStatus(PaymentStatus.UNPAID);
        }

        return paymentRecordRepository.save(paymentRecord);
    }

    // Get payment record by ID
    public PaymentRecord getPaymentById(UUID paymentRecordId) {
        return paymentRecordRepository.findById(paymentRecordId)
                .orElseThrow(() -> new RuntimeException("Payment record not found with id: " + paymentRecordId));
    }

    // Get all payment records
    public List<PaymentRecord> getAllPayments() {
        return paymentRecordRepository.findAll();
    }

    // Update a payment record (amount, status, method, proof URL)
    public PaymentRecord updatePayment(UUID paymentRecordId, PaymentRecord updatedPayment, Users admin) {
        PaymentRecord existingPayment = getPaymentById(paymentRecordId);

        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setMethod(updatedPayment.getMethod());
        existingPayment.setStatus(updatedPayment.getStatus());
        existingPayment.setProofOfPaymentUrl(updatedPayment.getProofOfPaymentUrl());

        // Set the admin who performed the update
        if (admin != null) {
            existingPayment.setVerifiedByAdmin(admin);
        }

        return paymentRecordRepository.save(existingPayment);
    }

    // Delete a payment record
    public void deletePayment(UUID paymentRecordId) {
        PaymentRecord paymentRecord = getPaymentById(paymentRecordId);
        paymentRecordRepository.delete(paymentRecord);
    }

    // Get all payments for a specific student
    public List<PaymentRecord> getPaymentsByStudent(UUID studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        return student.getPaymentRecords(); // assuming Student entity has getPaymentRecords()
    }

    // Optional: verify a payment by admin
    public PaymentRecord verifyPayment(UUID paymentRecordId, Users admin) {
        PaymentRecord paymentRecord = getPaymentById(paymentRecordId);

        paymentRecord.setStatus(PaymentStatus.PAID);
        paymentRecord.setVerifiedByAdmin(admin);

        return paymentRecordRepository.save(paymentRecord);
    }
}

