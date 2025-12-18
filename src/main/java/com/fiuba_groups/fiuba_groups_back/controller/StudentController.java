package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.service.FriendRequestService;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("students")
public class StudentController {

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private UserService userService;

    @GetMapping("/me/friends")
    public ResponseEntity<?> getMyFriends(Authentication auth) {
        try {
            Long studentId = getStudentIdFromAuth(auth);
            List<Student> friends = friendRequestService.getFriends(studentId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    private Long getStudentIdFromAuth(Authentication auth) {
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("Usuario no autenticado");
        }
        User user = userService.getUserByEmail(auth.getName());
        if (user.getStudent() == null) {
            throw new RuntimeException("El usuario no tiene un perfil de estudiante");
        }
        return user.getStudent().getId();
    }
}
