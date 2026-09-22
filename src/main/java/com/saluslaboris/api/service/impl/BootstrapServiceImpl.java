package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.config.BootstrapProperties;
import com.saluslaboris.api.dto.PersonaRequest;
import com.saluslaboris.api.dto.PersonaResponse;
import com.saluslaboris.api.dto.UsuarioCreateDto;
import com.saluslaboris.api.entity.Acceso;
import com.saluslaboris.api.entity.AccesoId;
import com.saluslaboris.api.entity.Pagina;
import com.saluslaboris.api.entity.Rol;
import com.saluslaboris.api.repository.AccesoRepository;
import com.saluslaboris.api.repository.PaginaRepository;
import com.saluslaboris.api.repository.RolRepository;
import com.saluslaboris.api.repository.UsuarioRepository;
import com.saluslaboris.api.security.Permissions;
import com.saluslaboris.api.service.BootstrapService;
import com.saluslaboris.api.service.PersonaService;
import com.saluslaboris.api.service.UsuarioService;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BootstrapServiceImpl implements BootstrapService {

    private final UsuarioRepository usuarios;
    private final RolRepository roles;
    private final PaginaRepository paginas;
    private final AccesoRepository accesos;
    private final PersonaService personaService;
    private final UsuarioService usuarioService;
    private final Validator validator;

    public BootstrapServiceImpl(UsuarioRepository usuarios,
                                RolRepository roles,
                                PaginaRepository paginas,
                                AccesoRepository accesos,
                                PersonaService personaService,
                                UsuarioService usuarioService,
                                Validator validator) {
        this.usuarios = usuarios;
        this.roles = roles;
        this.paginas = paginas;
        this.accesos = accesos;
        this.personaService = personaService;
        this.usuarioService = usuarioService;
        this.validator = validator;
    }

    @Override
    @Transactional
    public void initialize(BootstrapProperties p) {
        // Si ya existen usuarios registrados en la base de datos, no ejecuta el seeder
        if (usuarios.count() > 0) {
            return;
        }

        var personaReq = new PersonaRequest(
            p.getTipoDocumento(),
            p.getNroDocumento(),
            p.getNombres(),
            p.getApellidoPaterno(),
            null,
            p.getFechaNacimiento(),
            null,
            null
        );

        if (!validator.validate(personaReq).isEmpty() || p.getUsername() == null) {
            throw new IllegalStateException("Las propiedades de ADMIN_* en la configuración no son válidas");
        }

        Rol rol = roles.findByNombre(Permissions.ADMIN).orElseGet(() -> {
            Rol nuevo = new Rol();
            nuevo.setNombre(Permissions.ADMIN);
            nuevo.setDescripcion("Administración total del sistema");
            nuevo.setEstado(true);
            return roles.saveAndFlush(nuevo);
        });

        if (!rol.isEstado()) {
            throw new IllegalStateException("El rol ADMINISTRADOR se encuentra inactivo");
        }

        for (String ruta : Permissions.CORE_PAGES) {
            Pagina pagina = paginas.findByRuta(ruta).orElseGet(() -> {
                Pagina nueva = new Pagina();
                nueva.setNombre(ruta.replace("/", "").toUpperCase());
                nueva.setRuta(ruta);
                nueva.setEstado(true);
                return paginas.saveAndFlush(nueva);
            });

            if (!pagina.isEstado()) {
                throw new IllegalStateException("La página " + ruta + " se encuentra inactiva");
            }

            AccesoId id = new AccesoId(rol.getId(), pagina.getId());
            if (!accesos.existsById(id)) {
                accesos.save(new Acceso(rol, pagina));
            }
        }

        PersonaResponse personaGuardada = personaService.crear(personaReq);
        usuarioService.crear(new UsuarioCreateDto(
            personaGuardada.id(),
            rol.getId(),
            p.getUsername(),
            p.getPassword()
        ));
    }
}