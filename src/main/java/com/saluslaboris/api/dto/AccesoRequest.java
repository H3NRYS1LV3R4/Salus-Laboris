package com.saluslaboris.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record AccesoRequest(
    @NotNull(message = "La lista de páginas no puede ser nula")
    List<@Positive(message = "Cada identificador de página debe ser un número positivo") Integer> idPaginas
) {}