# Salus Laboris API

Backend del Sistema Web de Salud Ocupacional para el Sprint 1: seguridad y gestión administrativa.

## Incluido

- Spring Boot 3.5.16 y Java 17.
- Entidades JPA para `persona`, `rol`, `pagina`, `usuario` y `acceso`.
- Repositorios Spring Data JPA.
- DTO y validación de entradas; las respuestas nunca exponen `password_hash`.
- Contraseñas protegidas con BCrypt.
- Inicio de sesión JWT HS256 stateless.
- Permisos vigentes por rol y página, consultados en cada petición.
- CRUD administrativo de personas, usuarios, roles y páginas.
- Asignación de páginas a roles.
- Desactivación lógica mediante `estado`.
- Pruebas de integración con H2 y validación del esquema con MySQL 8 en GitHub Actions.

El frontend Angular todavía no está incluido.

## Configuración en STS

Abre **Run → Run Configurations → Spring Boot App → Environment** y agrega:

| Variable | Ejemplo o propósito |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/salus_laboris` |
| `DB_USERNAME` | Usuario de MySQL |
| `DB_PASSWORD` | Contraseña de MySQL |
| `JWT_SECRET_BASE64` | Secreto aleatorio de 32 bytes o más, codificado en Base64 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:4200` |

Genera el secreto una sola vez en `jshell`:

```java
byte[] clave = new byte[32];
new java.security.SecureRandom().nextBytes(clave);
System.out.println(java.util.Base64.getEncoder().encodeToString(clave));
```

El repositorio no contiene contraseñas reales. Spring Boot tampoco carga `.env` automáticamente.

## Base de datos

La aplicación usa `spring.jpa.hibernate.ddl-auto=validate`: verifica las cinco tablas existentes, pero no las modifica. Si vas a trabajar con una base totalmente nueva, puedes ejecutar manualmente [database/schema.sql](database/schema.sql) una sola vez.

### Primer administrador

Si `usuario` está vacía, puedes pedir que la aplicación cree el primer administrador. Agrega temporalmente estas variables en STS:

| Variable | Valor requerido |
|---|---|
| `BOOTSTRAP_ENABLED` | `true` |
| `ADMIN_USERNAME` | Usuario de 3 a 50 caracteres |
| `ADMIN_PASSWORD` | Contraseña de al menos 12 caracteres y hasta 72 bytes |
| `ADMIN_TIPO_DOCUMENTO` | Por ejemplo `DNI` |
| `ADMIN_DOCUMENTO` | Documento único |
| `ADMIN_NOMBRES` | Nombres |
| `ADMIN_APELLIDO` | Apellido paterno |
| `ADMIN_FECHA_NACIMIENTO` | Fecha `AAAA-MM-DD` pasada |

Al iniciar, se crean el rol `ADMINISTRADOR`, las cinco páginas administrativas, sus accesos y el usuario. Después de un arranque correcto, cambia `BOOTSTRAP_ENABLED` a `false`. Si ya existe cualquier usuario, el proceso no cambia datos ni contraseñas.

## Endpoints principales

| Método | Ruta | Función |
|---|---|---|
| `POST` | `/api/v1/auth/login` | Iniciar sesión |
| `GET` | `/api/v1/auth/me` | Consultar perfil y páginas permitidas |
| `GET/POST` | `/api/v1/personas` | Listar o registrar personas |
| `GET/PUT` | `/api/v1/personas/{id}` | Consultar o actualizar una persona |
| `PATCH` | `/api/v1/personas/{id}/estado` | Activar o desactivar una persona |
| `GET/POST` | `/api/v1/usuarios` | Listar o registrar usuarios |
| `GET/PUT` | `/api/v1/usuarios/{id}` | Consultar o actualizar un usuario |
| `PATCH` | `/api/v1/usuarios/{id}/estado` | Activar o desactivar un usuario |
| `PUT` | `/api/v1/usuarios/{id}/password` | Restablecer contraseña |
| `GET/POST` | `/api/v1/roles` | Listar o registrar roles |
| `GET/POST` | `/api/v1/paginas` | Listar o registrar páginas |
| `GET/PUT` | `/api/v1/accesos/roles/{idRol}` | Consultar o reemplazar accesos del rol |

Los listados aceptan `page` desde 0 y `size` de 1 a 100. Todos los endpoints salvo login requieren:

```text
Authorization: Bearer <token>
```

Ejemplo de login:

```json
{
  "nombreUsuario": "admin",
  "password": "la contraseña configurada"
}
```

Ejemplo de asignación completa de páginas a un rol:

```json
{
  "idPaginas": [1, 2, 3]
}
```

El `PUT` reemplaza la asignación anterior de forma transaccional.

## Compilar y probar

PowerShell:

```powershell
.\mvnw.cmd clean verify
```

Git Bash:

```bash
bash mvnw clean verify
```

Las pruebas locales usan H2. GitHub Actions también levanta MySQL 8, ejecuta el esquema exacto del proyecto, valida los mapeos JPA y prueba autenticación, autorización, CRUD, estados, CORS y tokens inválidos.
