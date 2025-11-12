package com.example.GoSchool.service;

import com.example.GoSchool.constant.PaymentStatus;
import com.example.GoSchool.dtos.PaymentRecordDTO;
import com.example.GoSchool.dtos.PaymentUploadDTO;
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
    private final FileStorageService fileStorageService;

    @Autowired
    public PaymentRecordService(PaymentRecordRepository paymentRecordRepository,
                                StudentRepository studentRepository,
                                FileStorageService fileStorageService) {
        this.paymentRecordRepository = paymentRecordRepository;
        this.studentRepository = studentRepository;
        this.fileStorageService = fileStorageService;
    }


    // Create payment with file upload
    public PaymentRecord createPaymentWithFile(UUID studentId, PaymentUploadDTO paymentUploadDTO) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        // Validate file
        if (paymentUploadDTO.getProofOfPaymentFile().isEmpty()) {
            throw new RuntimeException("Proof of payment file is required");
        }

        // Store the file and get URL
        String fileUrl = fileStorageService.storeFile(paymentUploadDTO.getProofOfPaymentFile(), "payments");

        // Create payment record
        PaymentRecord paymentRecord = PaymentRecord.builder()
                .student(student)
                .amount(paymentUploadDTO.getAmount())
                .paymentDate(LocalDate.now())
                .method(paymentUploadDTO.getMethod())
                .status(PaymentStatus.PENDING) // Set to pending for admin verification
                .proofOfPaymentUrl(fileUrl)
                .verifiedByAdmin(null) // Not verified yet
                .build();

        return paymentRecordRepository.save(paymentRecord);
    }

    // Get all pending payments for admin verification
    public List<PaymentRecord> getPendingPayments() {
        return paymentRecordRepository.findByStatus(PaymentStatus.PENDING);
    }

    // Verify payment by admin
    public PaymentRecord verifyPayment(UUID paymentRecordId, Users admin) {
        PaymentRecord paymentRecord = getPaymentById(paymentRecordId);
        paymentRecord.setStatus(PaymentStatus.PAID);
        paymentRecord.setVerifiedByAdmin(admin);
        return paymentRecordRepository.save(paymentRecord);
    }

    // Reject payment by admin
    public PaymentRecord rejectPayment(UUID paymentRecordId, Users admin) {
        PaymentRecord paymentRecord = getPaymentById(paymentRecordId);
        paymentRecord.setStatus(PaymentStatus.UNPAID);
        paymentRecord.setVerifiedByAdmin(admin);
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
   /* public PaymentRecord verifyPayment(UUID paymentRecordId, Users admin) {
        PaymentRecord paymentRecord = getPaymentById(paymentRecordId);

        paymentRecord.setStatus(PaymentStatus.PAID);
        paymentRecord.setVerifiedByAdmin(admin);

        return paymentRecordRepository.save(paymentRecord);
    }*/
}

