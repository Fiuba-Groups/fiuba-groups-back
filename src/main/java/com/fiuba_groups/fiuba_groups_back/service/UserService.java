package com.fiuba_groups.fiuba_groups_back.service;

import com.fiuba_groups.fiuba_groups_back.exception.UserAlreadyExistsException;
import com.fiuba_groups.fiuba_groups_back.exception.NotInstitutionalEmailException;
import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;
import java.util.Optional;

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
        return userRepository.save(new User(email, hashedPassword));
    }

    public Optional<User> login(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }
}
