package com.saluslaboris.api.dto;

import java.util.List;

public record JwtResponse(
    String token,
    String tokenType,
    long expiresInSeconds,
    UsuarioResponseDto usuario,
    List<PaginaResponse> paginas
) {}