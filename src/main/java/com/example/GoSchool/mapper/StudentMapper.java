package com.example.GoSchool.mapper;

import com.example.GoSchool.dtos.PaymentRecordDTO;
import com.example.GoSchool.dtos.StudentDTO;
import com.example.GoSchool.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    private final DriverMapper driverMapper;
    private final PaymentRecordMapper paymentRecordMapper;


    @Autowired
    public StudentMapper(DriverMapper driverMapper,
                         PaymentRecordMapper paymentRecordMapper) {
        this.driverMapper = driverMapper;
        this.paymentRecordMapper = paymentRecordMapper;
    }

    public StudentDTO toDTO(Student student) {
        if (student == null) return null;

        StudentDTO dto = new StudentDTO();
        dto.setStudentUUID(student.getStudentUUID());
        dto.setStudentFirstName(student.getStudentFirstName());
        dto.setStudentSurname(student.getStudentSurname());
        dto.setSchoolName(student.getSchoolName());
        dto.setMonthlyPaymentAmount(student.getMonthlyPaymentAmount());
        dto.setPaymentStatus(student.getPaymentStatus());
        dto.setStudentGrade(student.getStudentGrade());

        // Parent mapping
        if (student.getParent() != null) {
            dto.setParentUUID(student.getParent().getParentUUID());
            dto.setParentName(student.getParent().getFirstName());
            dto.setParentPhoneNumber(student.getParent().getContact());
            dto.setParentEmail(student.getParent().getUserAccount() != null
                    ? student.getParent().getUserAccount().getEmail()
                    : null);

            if (student.getParent().getParentLocation() != null) {
                dto.setParentAddress(student.getParent().getParentLocation().getAddress() + ", " +
                        student.getParent().getParentLocation().getCity() + ", " +
                        student.getParent().getParentLocation().getProvince() + ", " +
                        student.getParent().getParentLocation().getPostalCode());
                dto.setParentCity(student.getParent().getParentLocation().getCity());
                dto.setParentPostalCode(student.getParent().getParentLocation().getPostalCode());
                dto.setParentProvince(student.getParent().getParentLocation().getProvince());
            }
        }

        // Payment records
        if (student.getPaymentRecords() != null) {
            List<PaymentRecordDTO> paymentDTOs = student.getPaymentRecords().stream()
                    .map(paymentRecordMapper::toDTO) // use mapper
                    .collect(Collectors.toList());
            dto.setPaymentRecordDTOS(paymentDTOs);
        }

        // Driver mapping
        if (student.getDriver() != null) {
            dto.setDriverDto(driverMapper.toDTO(student.getDriver()));
        }

        return dto;
    }

    public Student toEntity(StudentDTO dto) {
        if (dto == null) return null;

        Student student = new Student();
        student.setStudentUUID(dto.getStudentUUID());
        student.setStudentFirstName(dto.getStudentFirstName());
        student.setStudentSurname(dto.getStudentSurname());
        student.setSchoolName(dto.getSchoolName());
        student.setMonthlyPaymentAmount(dto.getMonthlyPaymentAmount());
        student.setPaymentStatus(dto.getPaymentStatus());
        student.setStudentGrade(dto.getStudentGrade());

        if (dto.getParentUUID() != null) {
            Parent parent = new Parent();
            parent.setParentUUID(dto.getParentUUID());
            parent.setFirstName(dto.getParentName());
            parent.setContact(dto.getParentPhoneNumber());

            Users userAccount = new Users();
            userAccount.setEmail(dto.getParentEmail());
            parent.setUserAccount(userAccount);

            Location location = new Location();
            location.setAddress(dto.getParentAddress() != null ? dto.getParentAddress().split(",")[0].trim() : null);
            location.setCity(dto.getParentCity());
            location.setProvince(dto.getParentProvince());
            location.setPostalCode(dto.getParentPostalCode());
            parent.setParentLocation(location);

            student.setParent(parent);
        }

        if (dto.getPaymentRecordDTOS() != null) {
            List<PaymentRecord> payments = dto.getPaymentRecordDTOS().stream()
                    .map(PaymentRecordDTO::toEntity)
                    .collect(Collectors.toList());
            student.setPaymentRecords(payments);
        }

        // Driver mapping using DriverMapper
        if (dto.getDriverDto() != null) {
            student.setDriver(driverMapper.toEntity(dto.getDriverDto()));
        }

        return student;
    }
}

