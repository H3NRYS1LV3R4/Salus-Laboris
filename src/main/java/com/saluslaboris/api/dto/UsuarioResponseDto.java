package com.saluslaboris.api.dto;

import java.time.LocalDateTime;

public record UsuarioResponseDto(
    Integer id,
    PersonaResponse persona,
    RolResponse rol,
    String nombreUsuario,
    boolean estado,
    LocalDateTime fechaRegistro
) {}