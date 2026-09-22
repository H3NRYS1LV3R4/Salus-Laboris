package com.saluslaboris.api.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class AccesoId implements Serializable {
    private static final long serialVersionUID = 1L;
    @Column(name = "id_rol", nullable = false)
    private Integer idRol;
    @Column(name = "id_pagina", nullable = false)
    private Integer idPagina;
}
