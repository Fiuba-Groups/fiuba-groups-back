package com.fiuba_groups.fiuba_groups_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "grades")
@Getter
@Setter
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // Subject name
    private Double grade;       // Grade value
    private String semester;    // Semester (e.g., "2C 2024")
    private Integer credits;    // Credits for the subject

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
