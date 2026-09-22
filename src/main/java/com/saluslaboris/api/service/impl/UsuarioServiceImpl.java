package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.dto.PageResponse;
import com.saluslaboris.api.dto.PasswordRequest;
import com.saluslaboris.api.dto.UsuarioCreateDto;
import com.saluslaboris.api.dto.UsuarioResponseDto;
import com.saluslaboris.api.dto.UsuarioUpdateDto;
import com.saluslaboris.api.entity.Persona;
import com.saluslaboris.api.entity.Rol;
import com.saluslaboris.api.entity.Usuario;
import com.saluslaboris.api.exception.BusinessException;
import com.saluslaboris.api.repository.PersonaRepository;
import com.saluslaboris.api.repository.RolRepository;
import com.saluslaboris.api.repository.UsuarioRepository;
import com.saluslaboris.api.service.UsuarioService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarios;
    private final PersonaRepository personas;
    private final RolRepository roles;
    private final PasswordEncoder encoder;

    public UsuarioServiceImpl(UsuarioRepository usuarios, PersonaRepository personas,
                              RolRepository roles, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.personas = personas;
        this.roles = roles;
        this.encoder = encoder;
    }

    @Override
    public PageResponse<UsuarioResponseDto> listar(int page, int size) {
        var consulta = usuarios.findAll(PageRequest.of(page, size, Sort.by("id")));
        return PageResponse.of(consulta.map(DtoMapper::usuario));
    }

    @Override
    public UsuarioResponseDto obtener(Integer id) {
        return DtoMapper.usuario(find(id));
    }

    @Override
    @Transactional
    public UsuarioResponseDto crear(UsuarioCreateDto dto) {
        if (usuarios.existsByNombreUsuario(dto.nombreUsuario())) {
            throw BusinessException.conflict("El nombre de usuario ya está registrado");
        }
        if (usuarios.existsByPersonaId(dto.idPersona())) {
            throw BusinessException.conflict("Esta persona ya cuenta con un usuario asignado");
        }

        Persona p = personas.findById(dto.idPersona())
            .orElseThrow(() -> BusinessException.missing("Persona"));
        if (!p.isEstado()) {
            throw BusinessException.conflict("No se puede asociar una cuenta a una persona inactiva");
        }

        Rol r = obtenerRolActivo(dto.idRol());

        Usuario u = new Usuario();
        u.setPersona(p);
        u.setRol(r);
        u.setNombreUsuario(dto.nombreUsuario());
        u.setPasswordHash(encoder.encode(dto.password()));
        return DtoMapper.usuario(usuarios.save(u));
    }

    @Override
    @Transactional
    public UsuarioResponseDto actualizar(Integer id, UsuarioUpdateDto dto, Integer actorId) {
        Usuario u = find(id);
        if (id.equals(actorId) && !u.getRol().getId().equals(dto.idRol())) {
            throw BusinessException.conflict("No puedes modificar tu propio rol");
        }
        if (usuarios.existsByNombreUsuarioAndIdNot(dto.nombreUsuario(), id)) {
            throw BusinessException.conflict("El nombre de usuario ya está en uso");
        }

        u.setRol(obtenerRolActivo(dto.idRol()));
        u.setNombreUsuario(dto.nombreUsuario());
        return DtoMapper.usuario(usuarios.save(u));
    }

    @Override
    @Transactional
    public UsuarioResponseDto cambiarEstado(Integer id, boolean estado, Integer actorId) {
        if (!estado && id.equals(actorId)) {
            throw BusinessException.conflict("No puedes desactivar tu propia cuenta de usuario");
        }

        Usuario u = find(id);
        u.setEstado(estado);
        return DtoMapper.usuario(usuarios.save(u));
    }

    @Override
    @Transactional
    public void cambiarPassword(Integer id, PasswordRequest dto) {
        Usuario u = find(id);
        u.setPasswordHash(encoder.encode(dto.password()));
        usuarios.save(u);
    }

    private Usuario find(Integer id) {
        return usuarios.findOneById(id)
            .orElseThrow(() -> BusinessException.missing("Usuario"));
    }

    private Rol obtenerRolActivo(Integer idRol) {
        Rol r = roles.findById(idRol)
            .orElseThrow(() -> BusinessException.missing("Rol"));
        if (!r.isEstado()) {
            throw BusinessException.conflict("El rol seleccionado se encuentra inactivo");
        }
        return r;
    }
}