package com.fiuba_groups.fiuba_groups_back.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "group_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "from_student_id", "to_student_id"})
})
@Getter
@Setter
public class GroupRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_group"))
    private Group group;

    @ManyToOne
    @JoinColumn(name = "from_student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_from_student"))
    private Student fromStudent;

    @ManyToOne
    @JoinColumn(name = "to_student_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rating_to_student"))
    private Student toStudent;

    @Column(nullable = false)
    private int rating; // 1-5 estrellas

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
