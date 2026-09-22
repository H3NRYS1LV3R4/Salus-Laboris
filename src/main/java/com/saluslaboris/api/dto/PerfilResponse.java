package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record PerfilResponse(
    UsuarioResponseDto usuario, List<PaginaResponse> paginas
) {  }
