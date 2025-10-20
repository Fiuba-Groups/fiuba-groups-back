package com.fiuba_groups.fiuba_groups_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Student {
    @Id private String id;
    private int register; // padrón

    @ManyToMany(mappedBy = "members")
    private List<Group> groups = new ArrayList<Group>();
}
