package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseCreateRequest {
    private String commission;
    private Boolean active;
    private String subjectCode;
}