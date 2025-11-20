package com.fiuba_groups.fiuba_groups_back.exception;

public class NotInstitutionalEmailException extends RuntimeException {
    public NotInstitutionalEmailException(String message) {
        super(message);
    }
}
