package com.fiuba_groups.fiuba_groups_back.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String email;
    private String password;
    private String firstName;  // Para registro
    private String lastName;   // Para registro
    private Integer padron;    // Para registro
}
