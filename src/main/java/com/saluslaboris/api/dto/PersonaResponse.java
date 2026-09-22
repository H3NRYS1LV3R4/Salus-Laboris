package com.saluslaboris.api.dto;

import java.time.LocalDate;

public record PersonaResponse(
    Integer id,
    String tipoDocumento,
    String nroDocumento,
    String nombres,
    String apellidoPaterno,
    String apellidoMaterno,
    LocalDate fechaNacimiento,
    String correo,
    String telefono,
    boolean estado
) {}