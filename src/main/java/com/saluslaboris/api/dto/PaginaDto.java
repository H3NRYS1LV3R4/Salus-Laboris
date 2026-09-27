package com.saluslaboris.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PaginaDto(
    Integer id,
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Size(max = 150) @Pattern(regexp = "/[a-z0-9/-]+") String ruta,
    @Size(max = 100) String icono,
    Boolean estado
) {
}
