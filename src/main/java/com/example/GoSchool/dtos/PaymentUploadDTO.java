package com.example.GoSchool.dtos;

import com.example.GoSchool.constant.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentUploadDTO {
    private UUID studentId;
    private double amount;
    private PaymentMethod method;
    private MultipartFile proofOfPaymentFile;
}