package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingCreateRequest {
    private Long toStudentId;
    private int rating; // 1-5
}
