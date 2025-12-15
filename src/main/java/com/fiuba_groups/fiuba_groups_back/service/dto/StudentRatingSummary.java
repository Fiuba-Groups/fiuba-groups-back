package com.fiuba_groups.fiuba_groups_back.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class StudentRatingSummary {
    private Long studentId;
    private String studentName;
    private int studentRegister;
    private double averageRating;      // Promedio total
    private long totalRatings;          // Total de calificaciones recibidas
    private List<GroupRatingSummary> groupRatings;  // Promedio por grupo
}
