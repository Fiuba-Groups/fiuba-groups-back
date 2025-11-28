package com.fiuba_groups.fiuba_groups_back.model;

import lombok.Getter;

@Getter
public class UserUpdateRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
