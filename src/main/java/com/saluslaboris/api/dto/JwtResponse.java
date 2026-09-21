package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record JwtResponse(
    String accessToken, String tokenType, long expiresIn, UsuarioResponseDto usuario,
    List<PaginaResponse> paginas
) {  }
