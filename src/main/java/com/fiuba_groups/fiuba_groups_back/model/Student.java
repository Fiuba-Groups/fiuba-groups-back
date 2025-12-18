package com.fiuba_groups.fiuba_groups_back.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int register; // padrón
    private String name;  // nombre completo
    
    @Column(columnDefinition = "TEXT")
    private String avatarUrl; // URL o Base64 de la foto de perfil

    @ManyToMany(mappedBy = "members")
    @JsonManagedReference
    private List<Group> groups = new ArrayList<Group>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShowcasedGroup> showcasedGroups = new ArrayList<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Grade> grades = new ArrayList<Grade>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<AcademicDocument> documents = new ArrayList<AcademicDocument>();
}
