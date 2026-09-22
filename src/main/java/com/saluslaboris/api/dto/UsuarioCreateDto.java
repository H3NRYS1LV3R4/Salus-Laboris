package com.saluslaboris.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UsuarioCreateDto(
    @NotNull(message = "El id de la persona es obligatorio")
    @Positive(message = "El id de la persona debe ser válido")
    Integer idPersona,

    @NotNull(message = "El id del rol es obligatorio")
    @Positive(message = "El id del rol debe ser válido")
    Integer idRol,

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "El usuario solo puede contener letras, números, puntos, guiones o guión bajo")
    String nombreUsuario,

    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._-]).{8,64}$",
        message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
    )
    String password
) {}