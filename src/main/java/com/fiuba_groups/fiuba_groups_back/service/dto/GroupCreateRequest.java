package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupCreateRequest {
    private String description;
    private Long courseOfferingId;
}
