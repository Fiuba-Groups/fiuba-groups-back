package com.fiuba_groups.fiuba_groups_back.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CourseOffering {
    // TODO: necesitamos tipo de dato para los attr de las cursadas?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quarter;
    private String subject;
    private String course;

    @OneToMany(mappedBy = "courseOffering")
    @JsonManagedReference
    private List<Group> groups = new ArrayList<Group>();
}
