package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRequest {
    private String name;        // Subject name
    private Double grade;       // Grade value
    private String semester;    // Semester (e.g., "2C 2024")
    private Integer credits;    // Credits for the subject
}
