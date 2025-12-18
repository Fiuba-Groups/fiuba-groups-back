package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.Data;

@Data
public class ShowcasedGroupRequest {
    private Long groupId;
    private String description;
}
