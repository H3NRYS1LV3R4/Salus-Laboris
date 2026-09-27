package com.saluslaboris.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public final class PersonaDtos {

    private PersonaDtos() {
    }

    public record Request(
        @NotBlank @Size(max = 20) String tipoDocumento,
        @NotBlank @Size(max = 20) String nroDocumento,
        @NotBlank @Size(max = 100) String nombres,
        @NotBlank @Size(max = 100) String apellidoPaterno,
        @Size(max = 100) String apellidoMaterno,
        @NotNull @Past LocalDate fechaNacimiento,
        @Email @Size(max = 150) String correo,
        @Size(max = 20) String telefono
    ) {
    }

    public record Response(
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
    ) {
    }
}
