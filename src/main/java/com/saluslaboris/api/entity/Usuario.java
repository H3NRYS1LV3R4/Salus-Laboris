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
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false, unique = true)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(name = "nombre_usuario", nullable = false, length = 50, unique = true)
    private String nombreUsuario;
    @com.fasterxml.jackson.annotation.JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;
    // MySQL BOOLEAN es TINYINT(1), no BIT.
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "estado", nullable = false)
    private boolean estado = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private java.time.LocalDateTime fechaRegistro;

    @PrePersist
    void registrarFecha() {
        if (fechaRegistro == null) {
            fechaRegistro = java.time.LocalDateTime.now().withNano(0);
        }
    }

}
