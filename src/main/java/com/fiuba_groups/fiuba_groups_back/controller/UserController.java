package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.model.UserUpdateRequest;
import com.fiuba_groups.fiuba_groups_back.security.JwtUtil;
import com.fiuba_groups.fiuba_groups_back.service.UserService;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestBody UserUpdateRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = JwtUtil.validateAndGetEmail(token);

        try {
            userService.updateUser(email, request);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Usuario actualizado correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
