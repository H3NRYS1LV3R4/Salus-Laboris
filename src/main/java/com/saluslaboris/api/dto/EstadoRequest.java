package com.saluslaboris.api.dto;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(
    @NotNull(message = "El estado (true o false) es obligatorio")
    Boolean estado
) {}