package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.service.StudentService;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import com.fiuba_groups.fiuba_groups_back.service.dto.StudentUpdateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication auth) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            User user = userService.getUserByEmail(auth.getName());

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());

            if (user.getStudent() != null) {
                Student student = user.getStudent();
                response.put("student", Map.of(
                        "id", student.getId(),
                        "register", student.getRegister(),
                        "name", student.getName()));
            }

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<Map<String, Object>> response = users.stream()
                .map(user -> {
                    Map<String, Object> userMap = new java.util.HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("email", user.getEmail());

                    if (user.getStudent() != null) {
                        Student student = user.getStudent();
                        userMap.put("student", Map.of(
                                "id", student.getId(),
                                "register", student.getRegister(),
                                "name", student.getName()));
                    }

                    return userMap;
                })
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);

            Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());

            if (user.getStudent() != null) {
                Student student = user.getStudent();
                response.put("student", Map.of(
                        "id", student.getId(),
                        "register", student.getRegister(),
                        "name", student.getName()));
            }

            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/student")
    public ResponseEntity<?> updateMyStudent(Authentication auth, @RequestBody StudentUpdateRequest request) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            User user = userService.getUserByEmail(auth.getName());
            Student student = studentService.upsertStudentForUser(user, request);
            return ResponseEntity.ok(Map.of(
                    "id", student.getId(),
                    "register", student.getRegister(),
                    "name", student.getName()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
