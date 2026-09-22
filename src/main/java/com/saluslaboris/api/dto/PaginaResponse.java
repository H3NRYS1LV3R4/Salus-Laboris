package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record PaginaResponse(
    Integer id, String nombre, String ruta, String icono, boolean estado
) {  }
