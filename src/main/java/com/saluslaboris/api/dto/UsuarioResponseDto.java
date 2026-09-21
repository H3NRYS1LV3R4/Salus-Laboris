package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record UsuarioResponseDto(
    Integer id, PersonaResponse persona, RolResponse rol, String nombreUsuario,
    boolean estado, LocalDateTime fechaRegistro
) {  }
