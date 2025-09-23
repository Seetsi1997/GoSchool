package com.example.GoSchool.model;

import com.example.GoSchool.constant.PaymentMethod;
import com.example.GoSchool.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "payment_records")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paymentRecordId;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "paid_amount", nullable = false)
    private double amount;

    @Column(name = "paid_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @Column(name = "proof_of_payment", nullable = false)
    private String proofOfPaymentUrl;

    @ManyToOne
    @JoinColumn(name = "verified_payment_by_admin_id", nullable = false)
    private  Users verifiedByAdmin;
}

