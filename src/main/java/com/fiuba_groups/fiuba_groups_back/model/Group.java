package com.fiuba_groups.fiuba_groups_back.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "groups")
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private int memberCount = 1; // Inicia en 1 porque el creador es miembro
    private int maxMembers = 0;
    
    @Column(name = "creator_student_register")
    private int creatorStudentRegister = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupStatus status = GroupStatus.ACTIVE;

    @Column(name = "course_offering_id")
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_group_course_offering"))
    private Long courseOfferingId;

    @ManyToOne
    @JoinColumn(name = "course_offering_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"groups", "hibernateLazyInitializer", "handler"})
    private CourseOffering courseOffering;

    // Relación con el estudiante creador para obtener su nombre
    @ManyToOne
    @JoinColumn(name = "creator_student_register", referencedColumnName = "register", insertable = false, updatable = false)
    @JsonIgnoreProperties({"groups", "grades", "documents", "hibernateLazyInitializer", "handler"})
    private Student creatorStudent;

    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id"))
    @JsonIgnoreProperties({"groups", "grades", "documents", "hibernateLazyInitializer", "handler"})
    private List<Student> members = new ArrayList<Student>();
}
