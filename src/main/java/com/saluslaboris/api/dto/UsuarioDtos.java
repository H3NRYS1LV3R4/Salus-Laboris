package com.saluslaboris.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class UsuarioDtos {

    private UsuarioDtos() {
    }

    public record CreateRequest(
        @NotNull @Positive Integer idPersona,
        @NotNull @Positive Integer idRol,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,50}") String nombreUsuario,
        @NotBlank @Size(min = 12, max = 72) String password
    ) {
        @Override
        public String toString() {
            return "CreateRequest[credenciales ocultas]";
        }
    }

    public record UpdateRequest(
        @NotNull @Positive Integer idRol,
        @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,50}") String nombreUsuario
    ) {
    }

    public record Response(
        Integer id,
        PersonaDtos.Response persona,
        RolDto rol,
        String nombreUsuario,
        boolean estado,
        LocalDateTime fechaRegistro
    ) {
    }

    public record PasswordRequest(
        @NotBlank @Size(min = 12, max = 72) String password
    ) {
        @Override
        public String toString() {
            return "PasswordRequest[oculta]";
        }
    }
}
