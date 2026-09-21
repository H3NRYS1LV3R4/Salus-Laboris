package com.saluslaboris.api.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;

public record PersonaRequest(
    @NotBlank @Size(max = 20) String tipoDocumento,
    @NotBlank @Size(max = 20) String nroDocumento,
    @NotBlank @Size(max = 100) String nombres,
    @NotBlank @Size(max = 100) String apellidoPaterno,
    @Size(max = 100) String apellidoMaterno,
    @NotNull @Past LocalDate fechaNacimiento,
    @Email @Size(max = 150) String correo,
    @Size(max = 20) String telefono
) {  }
