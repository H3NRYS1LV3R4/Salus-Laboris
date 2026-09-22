package com.saluslaboris.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PersonaRequest(
    @NotBlank(message = "El tipo de documento es obligatorio")
    @Pattern(regexp = "^(DNI|CE|PASAPORTE)$", message = "El tipo de documento debe ser DNI, CE o PASAPORTE")
    String tipoDocumento,

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z]{8,20}$", message = "El número de documento debe tener entre 8 y 20 caracteres alfanuméricos")
    String nroDocumento,

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(min = 2, max = 100, message = "Los nombres deben tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Los nombres solo deben contener letras")
    String nombres,

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido paterno debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido paterno solo debe contener letras")
    String apellidoPaterno,

    @Size(max = 100, message = "El apellido materno no debe exceder los 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "El apellido materno solo debe contener letras")
    String apellidoMaterno,

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    LocalDate fechaNacimiento,

    @Email(message = "El formato de correo no es válido")
    @Size(max = 150, message = "El correo no debe exceder los 150 caracteres")
    String correo,

    @Pattern(regexp = "^(\\+?[0-9]{9,15})?$", message = "El teléfono debe contener entre 9 y 15 dígitos numéricos")
    String telefono
) {}