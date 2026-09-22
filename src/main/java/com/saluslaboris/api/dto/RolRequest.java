package com.saluslaboris.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RolRequest(
    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre del rol debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "El rol solo puede contener letras, números o guiones bajos")
    String nombre,

    @Size(max = 200, message = "La descripción no puede superar los 200 caracteres")
    String descripcion
) {}