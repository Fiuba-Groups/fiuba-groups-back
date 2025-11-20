package com.fiuba_groups.fiuba_groups_back;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.fiuba_groups.fiuba_groups_back.model.User;
import com.fiuba_groups.fiuba_groups_back.repository.UserRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        try {
              repo.save(new User("admin@fi.uba.ar", encoder.encode("1234")));
        } catch (Exception e) {
            // Ignore
        }
    }
}
