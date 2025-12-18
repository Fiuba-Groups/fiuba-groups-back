package com.fiuba_groups.fiuba_groups_back.repository;

import com.fiuba_groups.fiuba_groups_back.model.ShowcasedGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowcasedGroupRepository extends JpaRepository<ShowcasedGroup, Long> {
    List<ShowcasedGroup> findByStudentId(Long studentId);
    void deleteByStudentId(Long studentId);
}
