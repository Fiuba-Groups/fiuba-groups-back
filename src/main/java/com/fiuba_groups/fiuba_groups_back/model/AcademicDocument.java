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

import java.time.LocalDateTime;

@Entity
@Table(name = "academic_documents")
@Getter
@Setter
public class AcademicDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String type;
    private String fileName;       // Original file name
    private String fileUrl;        // Path/URL where file is stored
    private LocalDateTime uploadDate;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
}
