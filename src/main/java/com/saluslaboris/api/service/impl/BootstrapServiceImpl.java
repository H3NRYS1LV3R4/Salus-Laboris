package com.saluslaboris.api.service.impl;


import com.saluslaboris.api.config.BootstrapProperties;
import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.entity.*;
import com.saluslaboris.api.repository.*;
import com.saluslaboris.api.security.*;
import com.saluslaboris.api.service.*;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BootstrapServiceImpl implements BootstrapService {
    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PaginaRepository paginas;
    private final AccesoRepository accesos;
    private final PersonaService personaService;
    private final UsuarioService usuarioService;
    private final Validator validator;

    @Override @Transactional
    public void initialize(BootstrapProperties p) {
        // Solo inicializa una instalación sin cuentas; nunca restablece una contraseña existente.
        if (usuarios.count() > 0) return;
        var persona = new PersonaRequest(p.getTipoDocumento(), p.getNroDocumento(), p.getNombres(),
            p.getApellidoPaterno(), null, p.getFechaNacimiento(), null, null);
        if (!validator.validate(persona).isEmpty() || p.getUsername() == null
                || !p.getUsername().matches("[A-Za-z0-9._-]{3,50}")) {
            throw new IllegalStateException("Completa los datos válidos de ADMIN_* para crear el primer administrador");
        }
        Passwords.validate(p.getPassword());
        Rol rol = roles.findByNombre(Permissions.ADMIN).orElseGet(() -> {
            Rol nuevo = new Rol(); nuevo.setNombre(Permissions.ADMIN);
            nuevo.setDescripcion("Administración de seguridad y gestión administrativa");
            return roles.saveAndFlush(nuevo);
        });
        if (!rol.isEstado()) throw new IllegalStateException("El rol ADMINISTRADOR está inactivo");
        for (String ruta : Permissions.CORE_PAGES.stream().sorted().toList()) {
            Pagina pagina = paginas.findByRuta(ruta).orElseGet(() -> {
                Pagina nueva = new Pagina(); nueva.setNombre(ruta.substring(1)); nueva.setRuta(ruta);
                return paginas.saveAndFlush(nueva);
            });
            if (!pagina.isEstado()) throw new IllegalStateException("Una página de administración está inactiva");
            AccesoId id = new AccesoId(rol.getId(), pagina.getId());
            if (!accesos.existsById(id)) accesos.save(new Acceso(rol, pagina));
        }
        PersonaResponse guardada = personaService.crear(persona);
        usuarioService.crear(new UsuarioCreateDto(guardada.id(), rol.getId(), p.getUsername(), p.getPassword()));
    }
}
