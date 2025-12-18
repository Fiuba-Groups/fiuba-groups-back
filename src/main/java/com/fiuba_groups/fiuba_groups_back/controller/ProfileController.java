package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.model.Group;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
            // Intentar buscar por ID de usuario primero
            User user;
            try {
                user = userService.getUserById(id);
            } catch (ResourceNotFoundException e) {
                // Si no se encuentra por ID de usuario, intentar por ID de estudiante
                // Esto es necesario porque el frontend a veces usa el ID del estudiante (friend.id)
                // como si fuera el ID del usuario en las rutas /user/:userId
                user = userService.getUserByStudentId(id);
            }

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
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }

    @GetMapping("/me/groups")
    public ResponseEntity<?> getMyGroups(Authentication auth) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }
            User user = userService.getUserByEmail(auth.getName());
            if (user.getStudent() == null) {
                return ResponseEntity.ok(java.util.Collections.emptyList());
            }
            Student student = user.getStudent();
            List<Group> groups = student.getGroups();
            return ResponseEntity.ok(groups);
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

    @PostMapping("/me/avatar")
    public ResponseEntity<?> uploadAvatar(Authentication auth, @RequestParam("avatar") MultipartFile file) {
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

            // Convertir la imagen a Base64 Data URL
            String base64Image = java.util.Base64.getEncoder().encodeToString(file.getBytes());
            String contentType = file.getContentType() != null ? file.getContentType() : "image/png";
            String avatarUrl = "data:" + contentType + ";base64," + base64Image;

            // Guardar en el estudiante
            Student student = user.getStudent();
            student.setAvatarUrl(avatarUrl);
            studentService.save(student);

            return ResponseEntity.ok(Map.of("avatarUrl", avatarUrl));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error processing image file"));
        }
    }

    /**
     * Obtiene el email de un compañero de grupo.
     * Solo funciona si el usuario actual comparte al menos un grupo con el estudiante solicitado.
     */
    @GetMapping("/students/{studentId}/email")
    public ResponseEntity<?> getTeammateEmail(Authentication auth, @PathVariable Long studentId) {
        try {
            if (auth == null || auth.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "User not authenticated"));
            }

            User currentUser = userService.getUserByEmail(auth.getName());
            if (currentUser.getStudent() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "User does not have a student profile"));
            }

            // Verificar que el estudiante solicitado comparte al menos un grupo con el usuario actual
            Student currentStudent = currentUser.getStudent();
            List<Group> currentUserGroups = currentStudent.getGroups();
            
            boolean sharesGroup = currentUserGroups.stream()
                    .flatMap(group -> group.getMembers().stream())
                    .anyMatch(member -> member.getId().equals(studentId));

            if (!sharesGroup) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only get email of teammates in your groups"));
            }

            // Obtener el email del estudiante solicitado
            User targetUser = userService.getUserByStudentId(studentId);
            return ResponseEntity.ok(Map.of("email", targetUser.getEmail()));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getUserByStudentId(@PathVariable Long studentId) {
        try {
            User user = userService.getUserByStudentId(studentId);

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
                    .body(Map.of("error", "Usuario no encontrado"));
        }
    }
}
