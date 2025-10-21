package com.fiuba_groups.fiuba_groups_back.repository;

import com.fiuba_groups.fiuba_groups_back.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {}
