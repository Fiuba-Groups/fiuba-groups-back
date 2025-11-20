package com.fiuba_groups.fiuba_groups_back.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"quarter", "year", "course_id"})
})
@Getter
@Setter
public class CourseOffering {
    // TODO: necesitamos tipo de dato para los attr de las cursadas?
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String quarter;
    private String year;

    @Column(name = "course_id")
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_course_offering_course"))
    private Long courseId;

    @ManyToOne
    @JoinColumn(name = "course_id", insertable = false, updatable = false)
    private Course courseEntity;

    @OneToMany(mappedBy = "courseOffering")
    @JsonManagedReference
    private List<Group> groups = new ArrayList<Group>();
}
