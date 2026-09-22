package com.saluslaboris.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "acceso")
public class Acceso {

    @EmbeddedId
    private AccesoId id;

    @MapsId("idRol")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @MapsId("idPagina")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pagina", nullable = false)
    private Pagina pagina;

    public Acceso(Rol rol, Pagina pagina) {
        this.rol = rol;
        this.pagina = pagina;
        this.id = new AccesoId(rol.getId(), pagina.getId());
    }

}
