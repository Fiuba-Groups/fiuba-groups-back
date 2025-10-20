package com.fiuba_groups.fiuba_groups_back.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class CourseOffering {
    // TODO: necesitamos tipo de dato para los attr de las cursadas?
    @Id private String id;
    private String quarter;
    private String subject;
    private String course;

    @OneToMany(mappedBy = "courseOffering") private List<Group> groups = new ArrayList<Group>();
}
