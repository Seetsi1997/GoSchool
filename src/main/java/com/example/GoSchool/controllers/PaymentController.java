package com.example.GoSchool.controllers;

import com.example.GoSchool.constant.PaymentMethod;
import com.example.GoSchool.dtos.PaymentRecordDTO;
import com.example.GoSchool.dtos.PaymentUploadDTO;
import com.example.GoSchool.model.PaymentRecord;
import com.example.GoSchool.model.Student;
import com.example.GoSchool.service.PaymentRecordService;
import com.example.GoSchool.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/api/payments")
public class PaymentController {

    private final PaymentRecordService paymentRecordService;
    private final StudentService studentService;

    @Autowired
    public PaymentController(PaymentRecordService paymentRecordService, StudentService studentService) {
        this.paymentRecordService = paymentRecordService;
        this.studentService = studentService;
    }
    // Parent uploads proof of payment - MAKE SURE THIS ENDPOINT EXISTS
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadProofOfPayment(
            @RequestParam("studentId") UUID studentId,
            @RequestParam("amount") double amount,
            @RequestParam("method") PaymentMethod method,
            @RequestParam("file") MultipartFile file) {

        try {
            // Verify student exists
            Student student = studentService.getStudentById(studentId);

            PaymentUploadDTO uploadDTO = new PaymentUploadDTO(studentId, amount, method, file);
            PaymentRecord paymentRecord = paymentRecordService.createPaymentWithFile(studentId, uploadDTO);

            return ResponseEntity.ok(Map.of(
                    "message", "Proof of payment uploaded successfully",
                    "paymentId", paymentRecord.getPaymentRecordId(),
                    "status", paymentRecord.getStatus()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Get payment history for a student
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentPayments(@PathVariable UUID studentId) {
        try {
            List<PaymentRecord> payments = paymentRecordService.getPaymentsByStudent(studentId);
            List<PaymentRecordDTO> paymentDTOs = payments.stream()
                    .map(PaymentRecordDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(paymentDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
