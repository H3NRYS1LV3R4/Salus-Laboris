package com.saluslaboris.api.dto;

public record PaginaResponse(
    Integer id,
    String nombre,
    String ruta,
    String icono,
    boolean estado
) {}