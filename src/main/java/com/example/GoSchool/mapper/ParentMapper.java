package com.example.GoSchool.mapper;

import com.example.GoSchool.dtos.ParentDTO;
import com.example.GoSchool.dtos.StudentDTO;
import com.example.GoSchool.model.Parent;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ParentMapper {

    public ParentDTO toDTO(Parent parent) {
        ParentDTO dto = new ParentDTO();
        dto.setParentUUID(parent.getParentUUID());
        dto.setFirstName(parent.getFirstName());
        dto.setSurname(parent.getSurname());
        dto.setContact(parent.getContact());
        dto.setEmail(parent.getUserAccount().getEmail());
        dto.setRole(parent.getUserAccount().getRole());;
        dto.setUserId(parent.getUserAccount().getUuid());

        // map location
        if (parent.getParentLocation() != null) {
            dto.setCity(parent.getParentLocation().getCity());
            dto.setAddress(parent.getParentLocation().getAddress());
            dto.setPostalCode(parent.getParentLocation().getPostalCode());
            dto.setSuburb(parent.getParentLocation().getSuburb());
            dto.setProvince(parent.getParentLocation().getProvince());
            dto.setLocationUUID(parent.getParentLocation().getLocationUUID());
        }

        // map children to StudentDTO
        if (parent.getChildren() != null) {
            List<StudentDTO> studentDTOs = parent.getChildren().stream()
                    .map(StudentDTO::new) // pass entity to DTO constructor
                    .collect(Collectors.toList());
            dto.setStudentDTOList(studentDTOs);
        }

        return dto;
    }

    public List<ParentDTO> toDTOList(List<Parent> parents) {
        return parents.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
