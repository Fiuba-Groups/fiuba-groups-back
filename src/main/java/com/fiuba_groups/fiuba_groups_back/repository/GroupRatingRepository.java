package com.fiuba_groups.fiuba_groups_back.repository;

import com.fiuba_groups.fiuba_groups_back.model.GroupRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRatingRepository extends JpaRepository<GroupRating, Long> {
    
    // Buscar calificaciones recibidas por un estudiante
    List<GroupRating> findByToStudentId(Long studentId);
    
    // Buscar calificaciones recibidas por un estudiante en un grupo específico
    List<GroupRating> findByToStudentIdAndGroupId(Long studentId, Long groupId);
    
    // Buscar calificaciones dadas por un estudiante en un grupo
    List<GroupRating> findByFromStudentIdAndGroupId(Long fromStudentId, Long groupId);
    
    // Verificar si ya existe una calificación específica
    Optional<GroupRating> findByGroupIdAndFromStudentIdAndToStudentId(Long groupId, Long fromStudentId, Long toStudentId);
    
    // Calcular promedio de calificaciones de un estudiante
    @Query("SELECT AVG(r.rating) FROM GroupRating r WHERE r.toStudent.id = :studentId")
    Double getAverageRatingForStudent(@Param("studentId") Long studentId);
    
    // Calcular promedio de calificaciones de un estudiante en un grupo específico
    @Query("SELECT AVG(r.rating) FROM GroupRating r WHERE r.toStudent.id = :studentId AND r.group.id = :groupId")
    Double getAverageRatingForStudentInGroup(@Param("studentId") Long studentId, @Param("groupId") Long groupId);
    
    // Contar total de calificaciones recibidas
    long countByToStudentId(Long studentId);
}
