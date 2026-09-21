package com.saluslaboris.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pagina")
public class Pagina {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pagina")
    private Integer id;
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;
    @Column(name = "ruta", nullable = false, length = 150, unique = true)
    private String ruta;
    @Column(name = "icono", nullable = true, length = 100)
    private String icono;
    // MySQL BOOLEAN es TINYINT(1), no BIT.
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "estado", nullable = false)
    private boolean estado = true;

}
