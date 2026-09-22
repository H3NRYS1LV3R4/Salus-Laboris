package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record EstadoRequest(
    @NotNull Boolean estado
) {  }
