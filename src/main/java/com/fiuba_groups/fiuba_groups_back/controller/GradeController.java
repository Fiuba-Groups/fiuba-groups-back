package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Grade;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.service.GradeService;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import com.fiuba_groups.fiuba_groups_back.service.dto.GradeRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> addGrade(Authentication auth, @RequestBody GradeRequest request) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User user = userService.getUserByEmail(auth.getName());
            Grade grade = gradeService.addGradeForCurrentUser(user, request);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "id", grade.getId(),
                    "name", grade.getName(),
                    "grade", grade.getGrade(),
                    "semester", grade.getSemester(),
                    "credits", grade.getCredits()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getMyGrades(Authentication auth) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User user = userService.getUserByEmail(auth.getName());

            if (user.getStudent() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User does not have a student profile"));
            }

            java.util.List<Grade> grades = gradeService.getGradesForStudent(user.getStudent().getId());
            java.util.List<Map<String, Object>> response = grades.stream()
                    .map(grade -> Map.of(
                            "id", (Object) grade.getId(),
                            "name", (Object) grade.getName(),
                            "grade", (Object) grade.getGrade(),
                            "semester", (Object) grade.getSemester(),
                            "credits", (Object) grade.getCredits()))
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getGradesByUserId(@PathVariable Long userId) {
        try {
            User user = userService.getUserById(userId);

            if (user.getStudent() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User does not have a student profile"));
            }

            java.util.List<Grade> grades = gradeService.getGradesForStudent(user.getStudent().getId());
            java.util.List<Map<String, Object>> response = grades.stream()
                    .map(grade -> Map.of(
                            "id", (Object) grade.getId(),
                            "name", (Object) grade.getName(),
                            "grade", (Object) grade.getGrade(),
                            "semester", (Object) grade.getSemester(),
                            "credits", (Object) grade.getCredits()))
                    .collect(java.util.stream.Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
