# Salus Laboris API

Backend del Sistema Web de Salud Ocupacional, proyecto universitario.

## Estado de esta entrega

Primer paso del Sprint 1: configuración de Spring Boot 3.5.16, Java 17,
MySQL y dependencias JJWT 0.11.5. Incluye un perfil de pruebas H2
y compilación automática en GitHub Actions.

Todavía faltan las entidades de las cinco tablas, repositorios, DTO, servicios,
controladores, el filtro JWT y la configuración de seguridad stateless.
Las propiedades `app.jwt.*` quedan preparadas para el futuro `JwtService`;
no crean tokens ni habilitan un endpoint de login. Spring Security conserva
su configuración predeterminada por ahora. Angular aún no está incluido.

La clase principal conserva su ubicación actual. Al crear las capas, habrá
que trasladarla y trasladar su prueba a `com.saluslaboris.api` para que
Spring descubra los componentes del paquete solicitado.

## Abrir en Eclipse / STS

1. Seleccionar **File → Import → Maven → Existing Maven Projects**.
2. Elegir la carpeta que contiene `pom.xml`.
3. Configurar el proyecto con JDK 17.
4. Pulsar con el botón derecho sobre el proyecto: **Maven → Update Project**.
5. En **Run → Run Configurations → Spring Boot App → Environment**,
   agregar las variables de la siguiente tabla.
6. Ejecutar `SalusLaborisApplication` como **Spring Boot App**.

| Variable | Valor |
|---|---|
| `DB_URL` | URL JDBC de tu base existente; por defecto `jdbc:mysql://localhost:3306/salus_laboris`. |
| `DB_USERNAME` | Tu usuario de MySQL. |
| `DB_PASSWORD` | La contraseña de ese usuario. |
| `JWT_SECRET_BASE64` | Clave aleatoria de al menos 32 bytes, codificada en Base64. |

El nombre real de tu base puede ser diferente de `salus_laboris`.
No incluyas comillas alrededor de los valores en la ventana Environment.
Spring Boot no carga archivos `.env` automáticamente.

Para generar la clave en tu computadora, ejecuta `jshell` con el JDK 17 y pega:

```java
byte[] clave = new byte[32];
new java.security.SecureRandom().nextBytes(clave);
System.out.println(java.util.Base64.getEncoder().encodeToString(clave));
```

Guarda el resultado en `JWT_SECRET_BASE64`; no lo subas al repositorio.
Sal de JShell con `/exit`.

## MySQL y esquema existente

Usar MySQL 8 y las tablas ya creadas del proyecto.
`spring.jpa.hibernate.ddl-auto=validate` permite comprobar los mapeos cuando
se agreguen las entidades, sin crear ni modificar tablas.
`spring.sql.init.mode=never` desactiva la ejecución automática de
`schema.sql` y `data.sql`. No se incluyen credenciales reales.

## Compilar y ejecutar pruebas

Desde PowerShell, en la raíz del proyecto:

```powershell
.\mvnw.cmd clean verify
```

Desde Git Bash / Linux:

```bash
bash mvnw clean verify
```

Maven Wrapper descarga Maven y las dependencias en la primera ejecución.
Se requiere acceso a Maven Central. El comando compila, ejecuta la prueba
existente de arranque del contexto y genera el JAR en `target/`.

La prueba utiliza `@ActiveProfiles("test")` y una base H2 en memoria,
sin credenciales locales. Esto comprueba el arranque básico de Spring;
no verifica la conexión con tu MySQL, los mapeos de las cinco tablas
ni un flujo de autenticación JWT. El perfil de prueba está en
`src/test/resources` y no se empaqueta en el JAR de la aplicación.

GitHub Actions ejecuta el mismo comando al abrir o actualizar un pull request
hacia `main` y al subir cambios a `main`. Consultar la pestaña **Actions**
o los checks del pull request para ver el resultado real.

## Referencia

[Requisitos oficiales de Spring Boot 3.5](https://docs.spring.io/spring-boot/3.5/system-requirements.html).
