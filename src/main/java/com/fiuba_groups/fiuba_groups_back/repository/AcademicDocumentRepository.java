package com.fiuba_groups.fiuba_groups_back.repository;

import com.fiuba_groups.fiuba_groups_back.model.AcademicDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicDocumentRepository extends JpaRepository<AcademicDocument, Long> {
    List<AcademicDocument> findByStudentId(Long studentId);
}
