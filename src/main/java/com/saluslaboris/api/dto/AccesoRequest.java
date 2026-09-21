package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record AccesoRequest(
    @NotNull @Size(max = 100) Set<@NotNull @Positive Integer> idPaginas
) {  }
