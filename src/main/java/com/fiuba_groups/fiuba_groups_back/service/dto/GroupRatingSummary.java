package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupRatingSummary {
    private Long groupId;
    private String groupTitle;
    private double averageRating;
    private int ratingCount;
}
