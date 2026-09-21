package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record UsuarioUpdateDto(
    @NotNull @Positive Integer idRol,
    @NotBlank @Pattern(regexp = "[A-Za-z0-9._-]{3,50}") String nombreUsuario
) {  }
