package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.BadRequestException;
import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.GroupRating;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.service.GroupRatingService;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import com.fiuba_groups.fiuba_groups_back.service.dto.RatingCreateRequest;
import com.fiuba_groups.fiuba_groups_back.service.dto.StudentRatingSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class RatingController {
    @Autowired private GroupRatingService groupRatingService;
    @Autowired private UserService userService;

    /**
     * Calificar a un compañero de grupo
     * POST /groups/{groupId}/ratings
     */
    @PostMapping("/groups/{groupId}/ratings")
    public ResponseEntity<?> rateGroupMember(
            Authentication auth,
            @PathVariable Long groupId,
            @RequestBody RatingCreateRequest request) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User user = userService.getUserByEmail(auth.getName());
            if (user.getStudent() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User does not have a student profile"));
            }

            GroupRating rating = groupRatingService.addRating(groupId, user.getStudent().getId(), request);
            return ResponseEntity.status(HttpStatus.CREATED).body(rating);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (BadRequestException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener resumen de calificaciones de un estudiante
     * GET /students/{studentId}/ratings
     */
    @GetMapping("/students/{studentId}/ratings")
    public ResponseEntity<?> getStudentRatings(@PathVariable Long studentId) {
        try {
            StudentRatingSummary summary = groupRatingService.getStudentRatingSummary(studentId);
            return ResponseEntity.ok(summary);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener resumen de calificaciones de un estudiante por su padrón (register)
     * GET /students/register/{register}/ratings
     */
    @GetMapping("/students/register/{register}/ratings")
    public ResponseEntity<?> getStudentRatingsByRegister(@PathVariable int register) {
        try {
            StudentRatingSummary summary = groupRatingService.getStudentRatingSummaryByRegister(register);
            return ResponseEntity.ok(summary);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener compañeros pendientes de calificar en un grupo
     * GET /groups/{groupId}/ratings/pending
     */
    @GetMapping("/groups/{groupId}/ratings/pending")
    public ResponseEntity<?> getPendingRatings(Authentication auth, @PathVariable Long groupId) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User user = userService.getUserByEmail(auth.getName());
            if (user.getStudent() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User does not have a student profile"));
            }

            List<Student> pendingStudents = groupRatingService.getPendingRatingsForStudent(
                    groupId, user.getStudent().getId());
            return ResponseEntity.ok(pendingStudents);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
