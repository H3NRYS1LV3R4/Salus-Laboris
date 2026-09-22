package com.saluslaboris.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PaginaRequest(
    @NotBlank(message = "El nombre de la página es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    String nombre,

    @NotBlank(message = "La ruta es obligatoria")
    @Pattern(regexp = "^/[a-zA-Z0-9/_-]*$", message = "La ruta debe comenzar con una barra '/' y contener caracteres válidos")
    @Size(max = 150, message = "La ruta no puede exceder los 150 caracteres")
    String ruta,

    @Size(max = 100, message = "El icono no puede superar los 100 caracteres")
    String icono
) {}