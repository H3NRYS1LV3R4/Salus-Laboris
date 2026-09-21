-- Esquema del Sprint 1. Ejecutar manualmente SOLO en una base nueva/vacía.
-- Si las cinco tablas ya existen, no ejecutar este archivo.
CREATE TABLE persona (
    id_persona INT AUTO_INCREMENT NOT NULL,
    tipo_documento VARCHAR(20) NOT NULL,
    nro_documento VARCHAR(20) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100) NULL,
    fecha_nacimiento DATE NOT NULL,
    correo VARCHAR(150) NULL,
    telefono VARCHAR(20) NULL,
    estado BOOLEAN NOT NULL DEFAULT 1,
    CONSTRAINT pk_persona PRIMARY KEY (id_persona),
    CONSTRAINT uq_persona_nro_documento UNIQUE (nro_documento)
);
CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200) NULL,
    estado BOOLEAN NOT NULL DEFAULT 1,
    CONSTRAINT pk_rol PRIMARY KEY (id_rol),
    CONSTRAINT uq_rol_nombre UNIQUE (nombre)
);
CREATE TABLE pagina (
    id_pagina INT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    ruta VARCHAR(150) NOT NULL,
    icono VARCHAR(100) NULL,
    estado BOOLEAN NOT NULL DEFAULT 1,
    CONSTRAINT pk_pagina PRIMARY KEY (id_pagina),
    CONSTRAINT uq_pagina_ruta UNIQUE (ruta)
);
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT NOT NULL,
    id_persona INT NOT NULL,
    id_rol INT NOT NULL,
    nombre_usuario VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT 1,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_usuario PRIMARY KEY (id_usuario),
    CONSTRAINT uq_usuario_persona UNIQUE (id_persona),
    CONSTRAINT uq_usuario_nombre_usuario UNIQUE (nombre_usuario),
    CONSTRAINT fk_usuario_persona FOREIGN KEY (id_persona) REFERENCES persona(id_persona),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol)
);
CREATE TABLE acceso (
    id_rol INT NOT NULL,
    id_pagina INT NOT NULL,
    CONSTRAINT pk_acceso PRIMARY KEY (id_rol, id_pagina),
    CONSTRAINT fk_acceso_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol),
    CONSTRAINT fk_acceso_pagina FOREIGN KEY (id_pagina) REFERENCES pagina(id_pagina)
);
