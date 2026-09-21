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
@Table(name = "persona")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_persona")
    private Integer id;
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private String tipoDocumento;
    @Column(name = "nro_documento", nullable = false, length = 20, unique = true)
    private String nroDocumento;
    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;
    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;
    @Column(name = "apellido_materno", nullable = true, length = 100)
    private String apellidoMaterno;
    @Column(name = "fecha_nacimiento", nullable = false)
    private java.time.LocalDate fechaNacimiento;
    @Column(name = "correo", nullable = true, length = 150)
    private String correo;
    @Column(name = "telefono", nullable = true, length = 20)
    private String telefono;
    // MySQL BOOLEAN es TINYINT(1), no BIT.
    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(name = "estado", nullable = false)
    private boolean estado = true;

}
