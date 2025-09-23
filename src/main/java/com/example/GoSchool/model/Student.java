package com.example.GoSchool.model;

import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID studentUUID;

    @Column(name = "student_first_name", nullable = false)
    private String studentFirstName;

    @Column(name = "student_surname", nullable = false)
    private String studentSurname;

    @Enumerated(EnumType.STRING)
    @Column(name = "learners_grade_id", nullable = false)
    private LearnersGrade studentGrade;

    @Column(name = "monthly_payment_amount", nullable = false)
    private double monthlyPaymentAmount;

    // Each student belongs to ONE parent
    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    // If PaymentStatus is ENUM
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentRecord> paymentRecords = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "driver_id")
    private Driver driver;
}
