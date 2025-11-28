package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentUpdateRequest {
    private int register; // padrón
    private String name;  // nombre completo
}
