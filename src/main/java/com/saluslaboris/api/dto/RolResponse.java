package com.saluslaboris.api.dto;

public record RolResponse(
    Integer id,
    String nombre,
    String descripcion,
    boolean estado
) {}