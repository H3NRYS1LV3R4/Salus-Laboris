package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record LoginRequest(
    @NotBlank @Size(max = 50) String nombreUsuario,
    @NotBlank @Size(max = 72) String password
) {
    @Override public String toString() { return "LoginRequest[credenciales ocultas]"; }
 }
