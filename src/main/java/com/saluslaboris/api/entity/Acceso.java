package com.saluslaboris.api.entity;

import jakarta.persistence.*;

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

    public Acceso() {
    }

    public Acceso(Rol rol, Pagina pagina) {
        this.rol = rol;
        this.pagina = pagina;
        Integer rolId = (rol != null) ? rol.getId() : null;
        Integer paginaId = (pagina != null) ? pagina.getId() : null;
        this.id = new AccesoId(rolId, paginaId);
    }

    public AccesoId getId() {
        return id;
    }

    public void setId(AccesoId id) {
        this.id = id;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public Pagina getPagina() {
        return pagina;
    }

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
    }
}