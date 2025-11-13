package com.fiuba_groups.fiuba_groups_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commission;
    private Boolean active;

    @Column(name = "subject_code")
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_subject"))
    private String subjectCode;

    @ManyToOne
    @JoinColumn(name = "subject_code", insertable = false, updatable = false)
    private Subject subject;
}