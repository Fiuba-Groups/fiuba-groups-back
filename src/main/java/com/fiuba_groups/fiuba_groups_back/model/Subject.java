package com.fiuba_groups.fiuba_groups_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Subject {
    @Id
    private String code;

    private String name;
    private String department;
}