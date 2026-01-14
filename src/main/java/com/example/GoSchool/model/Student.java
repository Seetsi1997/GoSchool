package com.example.GoSchool.model;

import com.example.GoSchool.constant.ApplicationStatus;
import com.example.GoSchool.constant.LearnersGrade;
import com.example.GoSchool.constant.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @Column(nullable = false)
    private String studentFirstName;

    @Column(nullable = false)
    private String studentSurname;

    @Enumerated(EnumType.STRING)
    private LearnersGrade studentGrade;

    @Column(nullable = false)
    private String schoolName;

    @Column(nullable = false)
    private double monthlyPaymentAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonManagedReference
    private Parent parent;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentRecord> paymentRecords = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    @JsonBackReference
    private Driver driver;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus transportApplicationStatus;


    @Override
    public String toString() {
        return "Student{" +
                "studentUUID=" + studentUUID +
                ", firstName='" + studentFirstName + '\'' +
                '}';
    }



}
