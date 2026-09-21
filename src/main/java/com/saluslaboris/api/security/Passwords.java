package com.saluslaboris.api.security;


import com.saluslaboris.api.exception.BusinessException;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpStatus;

public final class Passwords {
    private Passwords() {}
    public static void validate(String password) {
        if (password == null || password.isBlank() || password.length() < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new BusinessException(HttpStatus.BAD_REQUEST,
                "La contraseña debe tener al menos 12 caracteres y como máximo 72 bytes UTF-8");
        }
    }
}
