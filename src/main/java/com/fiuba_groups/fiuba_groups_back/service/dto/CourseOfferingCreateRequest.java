package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseOfferingCreateRequest {
    private String quarter;
    private String year;
    private String subject;
    private String course;
}
