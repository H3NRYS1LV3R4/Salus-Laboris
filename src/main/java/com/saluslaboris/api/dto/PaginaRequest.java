package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record PaginaRequest(
    @NotBlank @Size(max = 100) String nombre,
    @NotBlank @Size(max = 150) @Pattern(regexp = "/[a-z0-9/-]+") String ruta,
    @Size(max = 100) String icono
) {  }
