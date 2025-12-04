package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.exception.UserAlreadyExistsException;
import com.fiuba_groups.fiuba_groups_back.exception.NotInstitutionalEmailException;
import com.fiuba_groups.fiuba_groups_back.model.Student;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Optional;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Pattern VALID_EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@fi\\.uba\\.ar$"); // expresion regular que valida que no este vacio y que sea institucional

    public User register(String email, String rawPassword) {
        if (!VALID_EMAIL.matcher(email).matches()) {
            throw new NotInstitutionalEmailException("Solo se permiten correos institucionales @fi.uba.ar");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("El usuario ya existe");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(email, hashedPassword);
        
        // Crear Student automáticamente al registrar
        Student student = new Student();
        // Generar un número de padrón único (6 dígitos)
        student.setRegister(ThreadLocalRandom.current().nextInt(100000, 999999));
        // Usar la parte del email antes del @ como nombre por defecto
        String defaultName = email.split("@")[0].replace(".", " ");
        student.setName(defaultName);
        
        newUser.setStudent(student);
        
        return userRepository.save(newUser);
    }

    public Optional<User> login(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with email " + email + " not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + id + " not found"));
    }

    public User getUserByStudentId(Long studentId) {
        return userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with student id " + studentId + " not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
