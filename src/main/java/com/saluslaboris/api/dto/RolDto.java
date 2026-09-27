package com.saluslaboris.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RolDto(
    Integer id,
    @NotBlank @Pattern(regexp = "[A-Z][A-Z0-9_]{1,49}") String nombre,
    @Size(max = 200) String descripcion,
    Boolean estado
) {
}
