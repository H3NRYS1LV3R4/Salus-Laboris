package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record RolRequest(
    @NotBlank @Pattern(regexp = "[A-Z][A-Z0-9_]{1,49}") String nombre,
    @Size(max = 200) String descripcion
) {  }
