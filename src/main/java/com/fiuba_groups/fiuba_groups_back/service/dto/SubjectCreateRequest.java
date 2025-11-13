package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubjectCreateRequest {
    private String code;
    private String name;
    private String department;
}