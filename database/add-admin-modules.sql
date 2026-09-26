-- Habilita los módulos administrativos en una instalación existente.
-- Ejecutar una sola vez sobre la base salus_laboris.
USE salus_laboris;

INSERT INTO pagina (nombre, ruta, icono, estado)
VALUES
    ('Roles', '/roles', 'admin_panel_settings', 1),
    ('Páginas', '/paginas', 'web', 1),
    ('Accesos', '/accesos', 'lock', 1)
ON DUPLICATE KEY UPDATE
    nombre = VALUES(nombre),
    icono = VALUES(icono),
    estado = 1;

INSERT IGNORE INTO acceso (id_rol, id_pagina)
SELECT r.id_rol, p.id_pagina
FROM rol r
JOIN pagina p ON p.ruta IN ('/roles', '/paginas', '/accesos')
WHERE r.nombre IN ('ADMIN', 'ADMINISTRADOR')
  AND r.estado = 1
  AND p.estado = 1;

-- Verificación: deben mostrarse tres filas por cada rol administrador activo.
SELECT r.nombre AS rol, p.nombre AS pagina, p.ruta
FROM acceso a
JOIN rol r ON r.id_rol = a.id_rol
JOIN pagina p ON p.id_pagina = a.id_pagina
WHERE r.nombre IN ('ADMIN', 'ADMINISTRADOR')
  AND p.ruta IN ('/roles', '/paginas', '/accesos')
ORDER BY r.nombre, p.ruta;
