package com.saluslaboris.api.dto;

import java.util.List;

public record PerfilResponse(
    UsuarioResponseDto usuario,
    List<PaginaResponse> paginas
) {}