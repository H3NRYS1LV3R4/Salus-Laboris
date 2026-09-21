package com.saluslaboris.api;

import com.fasterxml.jackson.databind.*;
import com.saluslaboris.api.config.BootstrapProperties;
import com.saluslaboris.api.entity.*;
import com.saluslaboris.api.repository.*;
import com.saluslaboris.api.security.*;
import com.saluslaboris.api.service.BootstrapService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Sprint1IntegrationTests {
    private static final String PASSWORD = "SoloPruebas-12345";
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired UsuarioRepository usuarios;
    @Autowired PersonaRepository personas;
    @Autowired RolRepository roles;
    @Autowired PaginaRepository paginas;
    @Autowired AccesoRepository accesos;
    @Autowired PasswordEncoder encoder;
    @Autowired BootstrapService bootstrap;
    @Autowired JwtService jwt;
    @Autowired UsuarioDetailsService details;
    private Usuario admin;
    private String token;

    @BeforeEach
    void prepare() throws Exception {
        accesos.deleteAllInBatch(); usuarios.deleteAllInBatch();
        personas.deleteAllInBatch(); paginas.deleteAllInBatch(); roles.deleteAllInBatch();
        bootstrap.initialize(properties());
        admin = usuarios.findByNombreUsuario("admin.test").orElseThrow();
        token = login("admin.test", PASSWORD);
    }

    @Test void loginIsStatelessAndDoesNotExposeCredentials() throws Exception {
        var result = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("nombreUsuario", "admin.test", "password", PASSWORD))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.expiresIn").value(3600))
            .andExpect(jsonPath("$.paginas.length()").value(5))
            .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist())
            .andExpect(header().doesNotExist("Set-Cookie")).andReturn();
        assertThat(result.getRequest().getSession(false)).isNull();
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.usuario.nombreUsuario").value("admin.test"));
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("nombreUsuario", "admin.test", "password", "incorrecta"))))
            .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/usuarios")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/v1/usuarios").header("Authorization", "Bearer roto"))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.status").value(401));
    }

    @Test void nonAdminCannotManageSecurityEvenWithPageGrant() throws Exception {
        Rol role = role("OPERADOR");
        Usuario user = user(role, "operador.test", "TEST-02");
        for (String path : List.of("/usuarios", "/roles", "/paginas", "/accesos"))
            accesos.saveAndFlush(new Acceso(role, paginas.findByRuta(path).orElseThrow()));
        String userToken = login(user.getNombreUsuario(), PASSWORD);
        mvc.perform(get("/api/v1/usuarios").header("Authorization", "Bearer " + userToken))
            .andExpect(status().isForbidden());
        mvc.perform(get("/api/v1/personas").header("Authorization", "Bearer " + userToken))
            .andExpect(status().isForbidden());
        // Los nuevos permisos se leen de la base incluso con el mismo token.
        Acceso permission = accesos.saveAndFlush(new Acceso(role, paginas.findByRuta("/personas").orElseThrow()));
        mvc.perform(get("/api/v1/personas").header("Authorization", "Bearer " + userToken)).andExpect(status().isOk());
        accesos.deleteById(permission.getId());
        mvc.perform(get("/api/v1/personas").header("Authorization", "Bearer " + userToken)).andExpect(status().isForbidden());
    }

    @Test void inactiveUserPersonAndRoleRevokeAccess() throws Exception {
        Rol role = role("LECTOR");
        Usuario user = user(role, "lector.test", "TEST-03");
        String userToken = login(user.getNombreUsuario(), PASSWORD);
        user.setEstado(false); usuarios.saveAndFlush(user);
        unauthorized(userToken);
        mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("nombreUsuario", "lector.test", "password", PASSWORD))))
            .andExpect(status().isUnauthorized());
        user.setEstado(true); usuarios.saveAndFlush(user);
        Persona person = personas.findById(user.getPersona().getId()).orElseThrow();
        person.setEstado(false); personas.saveAndFlush(person);
        unauthorized(userToken);
        person.setEstado(true); personas.saveAndFlush(person);
        role.setEstado(false); roles.saveAndFlush(role);
        unauthorized(userToken);
    }

    @Test void passwordResetRevokesExistingTokenAndStoresBCrypt() throws Exception {
        Usuario user = user(role("LECTOR"), "reset.test", "TEST-04");
        String before = login(user.getNombreUsuario(), PASSWORD);
        String next = "OtraClavePrueba-9876";
        mvc.perform(put("/api/v1/usuarios/{id}/password", user.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("password", next))))
            .andExpect(status().isNoContent());
        unauthorized(before);
        assertThat(encoder.matches(next, usuarios.findOneById(user.getId()).orElseThrow().getPasswordHash())).isTrue();
        assertThat(login(user.getNombreUsuario(), next)).isNotBlank();
    }

    @Test void jwtRejectsExpiredForeignIssuerForgedAndMissingExpiration() throws Exception {
        var key = Keys.hmacShaKeyFor(Decoders.BASE64.decode("MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY="));
        var claims = jwt.parse(token);
        String fingerprint = claims.get("cv", String.class);
        for (String kind : List.of("expired", "issuer", "unsigned", "no-exp")) {
            var builder = Jwts.builder().setSubject(admin.getId().toString())
                .setIssuer(kind.equals("issuer") ? "otro-emisor" : "salus-laboris-api")
                .setIssuedAt(Date.from(Instant.now().minusSeconds(60))).claim("cv", fingerprint);
            if (!kind.equals("no-exp")) builder.setExpiration(Date.from(Instant.now().plusSeconds(kind.equals("expired") ? -10 : 60)));
            if (!kind.equals("unsigned")) builder.signWith(key, SignatureAlgorithm.HS256);
            unauthorized(builder.compact());
        }
        String forged = Jwts.builder().setSubject(admin.getId().toString()).setIssuer("salus-laboris-api")
            .setIssuedAt(new Date()).setExpiration(Date.from(Instant.now().plusSeconds(60))).claim("cv", fingerprint)
            .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256)).compact();
        unauthorized(forged);
    }

    @Test void personAndUserCrudValidateDuplicatesAndSoftDisable() throws Exception {
        var body = personBody("TEST-05");
        MvcResult created = mvc.perform(post("/api/v1/personas").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)))
            .andExpect(status().isCreated()).andReturn();
        int personId = json.readTree(created.getResponse().getContentAsString()).get("id").asInt();
        mvc.perform(post("/api/v1/personas").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isConflict());
        body.put("nombres", "Nombre actualizado");
        mvc.perform(put("/api/v1/personas/{id}", personId).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body)))
            .andExpect(status().isOk()).andExpect(jsonPath("$.nombres").value("Nombre actualizado"));
        Rol role = role("LECTOR");
        Map<String,Object> input = new HashMap<>(Map.of("idPersona", personId, "idRol", role.getId(),
            "nombreUsuario", "nuevo.test", "password", PASSWORD));
        MvcResult account = mvc.perform(post("/api/v1/usuarios").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(input)))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.passwordHash").doesNotExist()).andReturn();
        int userId = json.readTree(account.getResponse().getContentAsString()).get("id").asInt();
        assertThat(encoder.matches(PASSWORD, usuarios.findOneById(userId).orElseThrow().getPasswordHash())).isTrue();
        input.put("nombreUsuario", "duplicado.test");
        mvc.perform(post("/api/v1/usuarios").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(input))).andExpect(status().isConflict());
        mvc.perform(put("/api/v1/usuarios/{id}", userId).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("idRol", role.getId(), "nombreUsuario", "editado.test"))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.nombreUsuario").value("editado.test"));
        String userToken = login("editado.test", PASSWORD);
        mvc.perform(patch("/api/v1/usuarios/{id}/estado", userId).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("estado", false))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value(false));
        unauthorized(userToken);
        assertThat(usuarios.existsById(userId)).isTrue();
        mvc.perform(get("/api/v1/usuarios?page=0&size=1").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test void invalidInputsReturn400AndMissingIds404() throws Exception {
        mvc.perform(post("/api/v1/personas").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.errors.nombres").exists());
        var future = personBody("TEST-06"); future.put("fechaNacimiento", "2999-01-01");
        mvc.perform(post("/api/v1/personas").header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(future))).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/personas?size=1000").header("Authorization", "Bearer " + token)).andExpect(status().isBadRequest());
        mvc.perform(get("/api/v1/personas/999999").header("Authorization", "Bearer " + token)).andExpect(status().isNotFound());
        mvc.perform(put("/api/v1/usuarios/{id}/password", admin.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("password", "á".repeat(40)))))
            .andExpect(status().isBadRequest());
    }

    @Test void accessReplacementIsAtomicIdempotentAndEnforced() throws Exception {
        Rol r = role("OPERADOR");
        Integer paginaId = paginas.findByRuta("/personas").orElseThrow().getId();
        String dto = json.writeValueAsString(Map.of("idPaginas", List.of(paginaId)));
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/api/v1/accesos/roles/{id}", r.getId()).header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON).content(dto))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1));
        }
        mvc.perform(put("/api/v1/accesos/roles/{id}", r.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("idPaginas", List.of(999999)))))
            .andExpect(status().isNotFound());
        assertThat(accesos.findPaginasActivas(r.getId())).hasSize(1);
        mvc.perform(put("/api/v1/accesos/roles/{id}", r.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("idPaginas", List.of()))))
            .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    @Test void reservedAdministrationCannotBeDisabledByAccident() throws Exception {
        mvc.perform(patch("/api/v1/usuarios/{id}/estado", admin.getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("estado", false)))).andExpect(status().isConflict());
        mvc.perform(patch("/api/v1/roles/{id}/estado", admin.getRol().getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("estado", false)))).andExpect(status().isConflict());
        mvc.perform(put("/api/v1/accesos/roles/{id}", admin.getRol().getId()).header("Authorization", "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("idPaginas", List.of())))).andExpect(status().isConflict());
    }

    @Test void bootstrapDoesNotResetExistingAdmin() {
        String before = usuarios.findOneById(admin.getId()).orElseThrow().getPasswordHash();
        BootstrapProperties p = properties(); p.setPassword("OtraClavePrueba-9876");
        bootstrap.initialize(p);
        assertThat(usuarios.count()).isEqualTo(1);
        assertThat(usuarios.findOneById(admin.getId()).orElseThrow().getPasswordHash()).isEqualTo(before);
    }

    @Test void corsAllowsAngularAndRejectsUnknownOrigins() throws Exception {
        mvc.perform(options("/api/v1/personas").header("Origin", "http://localhost:4200")
            .header("Access-Control-Request-Method", "GET").header("Access-Control-Request-Headers", "authorization"))
            .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:4200"));
        mvc.perform(options("/api/v1/personas").header("Origin", "https://otro.example")
            .header("Access-Control-Request-Method", "GET"))
            .andExpect(status().isForbidden());
    }

    private String login(String name, String password) throws Exception {
        var result = mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content(json.writeValueAsString(Map.of("nombreUsuario", name, "password", password))))
            .andExpect(status().isOk()).andReturn();
        return json.readTree(result.getResponse().getContentAsString()).get("accessToken").asText();
    }
    private void unauthorized(String bearer) throws Exception {
        mvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + bearer)).andExpect(status().isUnauthorized());
    }
    private Rol role(String name) {
        Rol r = new Rol(); r.setNombre(name); return roles.saveAndFlush(r);
    }
    private Usuario user(Rol role, String name, String document) {
        Persona p = new Persona(); p.setTipoDocumento("OTRO"); p.setNroDocumento(document);
        p.setNombres("Persona de prueba"); p.setApellidoPaterno("Prueba"); p.setFechaNacimiento(LocalDate.of(2000,1,1));
        personas.saveAndFlush(p);
        Usuario u = new Usuario(); u.setPersona(p); u.setRol(role); u.setNombreUsuario(name);
        u.setPasswordHash(encoder.encode(PASSWORD)); return usuarios.saveAndFlush(u);
    }
    private Map<String,Object> personBody(String document) {
        return new HashMap<>(Map.of("tipoDocumento", "OTRO", "nroDocumento", document,
            "nombres", "Persona de prueba", "apellidoPaterno", "Prueba", "fechaNacimiento", "2000-01-01"));
    }
    private BootstrapProperties properties() {
        BootstrapProperties p = new BootstrapProperties();
        p.setUsername("admin.test"); p.setPassword(PASSWORD); p.setTipoDocumento("OTRO");
        p.setNroDocumento("ADMIN-TEST"); p.setNombres("Administrador de prueba"); p.setApellidoPaterno("Prueba");
        p.setFechaNacimiento(LocalDate.of(2000,1,1)); return p;
    }
}
