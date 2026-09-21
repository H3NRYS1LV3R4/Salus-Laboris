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
@Table(name = "rol")
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer id;
    @Column(name = "nombre", nullable = false, length = 50, unique = true)
    private String nombre;
    @Column(name = "descripcion", nullable = true, length = 200)
    private String descripcion;
    // MySQL BOOLEAN es TINYINT(1), no BIT.
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "estado", nullable = false)
    private boolean estado = true;

}
