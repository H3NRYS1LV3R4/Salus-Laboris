package com.salusLaboris.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pagina")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pagina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagina")
    private Integer idPagina;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "ruta", nullable = false, unique = true, length = 150)
    private String ruta;

    @Column(name = "icono", length = 100)
    private String icono;

    @Column(name = "estado", nullable = false)
    private Boolean estado = true;
}