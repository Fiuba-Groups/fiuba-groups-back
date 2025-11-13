package com.fiuba_groups.fiuba_groups_back.model;

import lombok.Getter;

@Getter
public class JwtResponse {
    private String token;

    public JwtResponse(String token) {
        this.token = token;
    }
}
