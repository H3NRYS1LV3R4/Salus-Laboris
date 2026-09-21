package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record UsuarioCreateDto(
    @NotNull @Positive Integer idPersona,
    @NotNull @Positive Integer idRol,
    @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,50}") String nombreUsuario,
    @NotBlank @Size(min = 12, max = 72) String password
) {
    @Override public String toString() { return "UsuarioCreateDto[credenciales ocultas]"; }
 }
