package com.salusLaboris.api.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class AccesoId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "id_pagina")
    private Integer idPagina;

    public AccesoId() {
    }

    public AccesoId(Integer idRol, Integer idPagina) {
        this.idRol = idRol;
        this.idPagina = idPagina;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public Integer getIdPagina() {
        return idPagina;
    }

    public void setIdPagina(Integer idPagina) {
        this.idPagina = idPagina;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccesoId)) return false;
        AccesoId accesoId = (AccesoId) o;
        return Objects.equals(idRol, accesoId.idRol)
                && Objects.equals(idPagina, accesoId.idPagina);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idRol, idPagina);
    }
}