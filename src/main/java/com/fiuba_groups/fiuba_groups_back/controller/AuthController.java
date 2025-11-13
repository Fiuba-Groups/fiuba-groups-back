package com.fiuba_groups.fiuba_groups_back.controller;

import com.fiuba_groups.fiuba_groups_back.model.JwtResponse;
import com.fiuba_groups.fiuba_groups_back.model.LoginRequest;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.security.JwtUtil;
import com.fiuba_groups.fiuba_groups_back.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000") // por ahora en local
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest request) {
        try {
            User newUser = userService.register(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(Map.of("message", "Usuario registrado con éxito", "email", newUser.getEmail()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.login(request.getEmail(), request.getPassword())
                .map(user -> {
                    String token = JwtUtil.generateToken(user.getEmail());
                    return ResponseEntity.ok(new JwtResponse(token));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new JwtResponse("Credenciales inválidas")));
    }
}
