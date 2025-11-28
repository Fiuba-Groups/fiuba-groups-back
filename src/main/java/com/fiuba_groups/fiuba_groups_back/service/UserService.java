package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.ResourceNotFoundException;
import com.fiuba_groups.fiuba_groups_back.exception.UserAlreadyExistsException;
import com.fiuba_groups.fiuba_groups_back.exception.NotInstitutionalEmailException;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.model.UserUpdateRequest;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
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

        if(not_valid_password(rawPassword)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una simbolo y un número.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        return userRepository.save(new User(email, hashedPassword));
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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    private boolean not_valid_password(String password) {
        if (password.length() < 8) {
            return true;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSymbol = password.matches(".*[!@#$%^&*(),.?\":{}|<>].*");

        return !(hasUppercase && hasNumber && hasSymbol);
    }

    public void updateUser(String email, UserUpdateRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (req.getCurrentPassword() == null)
                throw new RuntimeException("Debes enviar la contraseña actual para cambiarla");

            if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword()))
                throw new RuntimeException("La contraseña actual no es correcta");

            if (req.getNewPassword().equals(req.getCurrentPassword()))
                throw new RuntimeException("La nueva contraseña no puede ser igual a la actual");

            if (!req.getNewPassword().equals(req.getConfirmNewPassword()))
                throw new RuntimeException("La confirmación de la nueva contraseña no coincide");

            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }

        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }

        userRepository.deleteById(userId);
    }
}
