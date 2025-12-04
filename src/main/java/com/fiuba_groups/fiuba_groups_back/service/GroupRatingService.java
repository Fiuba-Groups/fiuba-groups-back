package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Group;
import com.fiuba_groups.fiuba_groups_back.model.GroupRating;
import com.fiuba_groups.fiuba_groups_back.model.GroupStatus;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRatingRepository;
import com.fiuba_groups.fiuba_groups_back.repository.GroupRepository;
import com.fiuba_groups.fiuba_groups_back.repository.StudentRepository;
import com.fiuba_groups.fiuba_groups_back.service.dto.RatingCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.StudentRatingSummary;
import com.fiuba_groups.fiuba_groups_back.service.dto.GroupRatingSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupRatingService {
    @Autowired private GroupRatingRepository groupRatingRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private StudentRepository studentRepository;

    @Transactional
    public GroupRating addRating(Long groupId, Long fromStudentId, RatingCreateRequest request) {
        // Validar rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5");
        }

        // Buscar grupo
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group with id " + groupId + " not found"));

        // Verificar que el grupo está terminado
        if (group.getStatus() != GroupStatus.FINISHED) {
            throw new BadRequestException("Can only rate members in finished groups");
        }

        // Buscar estudiantes
        Student fromStudent = studentRepository.findById(fromStudentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id " + fromStudentId + " not found"));
        
        Student toStudent = studentRepository.findById(request.getToStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student with id " + request.getToStudentId() + " not found"));

        // Verificar que ambos son miembros del grupo
        if (!group.getMembers().contains(fromStudent)) {
            throw new BadRequestException("You are not a member of this group");
        }
        if (!group.getMembers().contains(toStudent)) {
            throw new BadRequestException("Target student is not a member of this group");
        }

        // Verificar que no se está calificando a sí mismo
        if (fromStudentId.equals(request.getToStudentId())) {
            throw new BadRequestException("You cannot rate yourself");
        }

        // Verificar que no existe ya una calificación
        if (groupRatingRepository.findByGroupIdAndFromStudentIdAndToStudentId(
                groupId, fromStudentId, request.getToStudentId()).isPresent()) {
            throw new BadRequestException("You have already rated this student in this group");
        }

        // Crear la calificación
        GroupRating rating = new GroupRating();
        rating.setGroup(group);
        rating.setFromStudent(fromStudent);
        rating.setToStudent(toStudent);
        rating.setRating(request.getRating());

        return groupRatingRepository.save(rating);
    }

    /**
     * Obtiene el resumen de calificaciones de un estudiante:
     * - Promedio total
     * - Cantidad de calificaciones
     * - Promedio por grupo (solo grupos terminados)
     */
    public StudentRatingSummary getStudentRatingSummary(Long studentId) {
        // Verificar que el estudiante existe
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student with id " + studentId + " not found"));

        Double averageRating = groupRatingRepository.getAverageRatingForStudent(studentId);
        long totalRatings = groupRatingRepository.countByToStudentId(studentId);

        // Obtener ratings por grupo
        List<GroupRating> allRatings = groupRatingRepository.findByToStudentId(studentId);
        
        // Agrupar por grupo y calcular promedios
        List<GroupRatingSummary> groupRatings = allRatings.stream()
                .collect(Collectors.groupingBy(r -> r.getGroup().getId()))
                .entrySet().stream()
                .map(entry -> {
                    Long groupId = entry.getKey();
                    List<GroupRating> ratings = entry.getValue();
                    Group group = ratings.get(0).getGroup();
                    
                    double avg = ratings.stream()
                            .mapToInt(GroupRating::getRating)
                            .average()
                            .orElse(0.0);
                    
                    return new GroupRatingSummary(
                            groupId,
                            group.getTitle(),
                            avg,
                            ratings.size()
                    );
                })
                .collect(Collectors.toList());

        return new StudentRatingSummary(
                studentId,
                student.getName(),
                student.getRegister(),
                averageRating != null ? averageRating : 0.0,
                totalRatings,
                groupRatings
        );
    }

    /**
     * Obtiene las calificaciones pendientes que un estudiante debe dar en un grupo
     */
    public List<Student> getPendingRatingsForStudent(Long groupId, Long studentId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group with id " + groupId + " not found"));

        if (group.getStatus() != GroupStatus.FINISHED) {
            return new ArrayList<>();
        }

        // Obtener estudiantes ya calificados por este estudiante en este grupo
        List<GroupRating> givenRatings = groupRatingRepository.findByFromStudentIdAndGroupId(studentId, groupId);
        List<Long> ratedStudentIds = givenRatings.stream()
                .map(r -> r.getToStudent().getId())
                .collect(Collectors.toList());

        // Retornar miembros que aún no han sido calificados (excluyendo al propio estudiante)
        return group.getMembers().stream()
                .filter(s -> !s.getId().equals(studentId))
                .filter(s -> !ratedStudentIds.contains(s.getId()))
                .collect(Collectors.toList());
    }
}
