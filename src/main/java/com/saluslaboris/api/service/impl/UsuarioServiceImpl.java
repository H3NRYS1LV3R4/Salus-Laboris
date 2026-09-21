package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.entity.*;
import com.saluslaboris.api.repository.*;
import com.saluslaboris.api.service.*;
import com.saluslaboris.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saluslaboris.api.security.Passwords;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiceImpl implements UsuarioService {
    private final UsuarioRepository usuarios;
    private final PersonaRepository personas;
    private final RolRepository roles;
    private final PasswordEncoder encoder;

    @Override public PageResponse<UsuarioResponseDto> listar(int page, int size) {
        return PageResponse.of(usuarios.findAll(PageRequest.of(page, size, Sort.by("id"))).map(DtoMapper::usuario));
    }
    @Override public UsuarioResponseDto obtener(Integer id) { return DtoMapper.usuario(find(id)); }
    @Override @Transactional
    public UsuarioResponseDto crear(UsuarioCreateDto dto) {
        Passwords.validate(dto.password());
        if (usuarios.existsByNombreUsuario(dto.nombreUsuario())) {
            throw BusinessException.conflict("El nombre de usuario ya está registrado");
        }
        if (usuarios.existsByPersonaId(dto.idPersona())) {
            throw BusinessException.conflict("Esta persona ya tiene una cuenta");
        }
        Persona p = personas.findById(dto.idPersona()).orElseThrow(() -> BusinessException.missing("Persona"));
        if (!p.isEstado()) throw BusinessException.conflict("La persona está inactiva");
        Usuario u = new Usuario();
        u.setPersona(p);
        u.setRol(activeRole(dto.idRol()));
        u.setNombreUsuario(dto.nombreUsuario());
        u.setPasswordHash(encoder.encode(dto.password()));
        return DtoMapper.usuario(usuarios.saveAndFlush(u));
    }
    @Override @Transactional
    public UsuarioResponseDto actualizar(Integer id, UsuarioUpdateDto dto, Integer actorId) {
        Usuario u = find(id);
        if (id.equals(actorId) && !u.getRol().getId().equals(dto.idRol())) {
            throw BusinessException.conflict("No puedes cambiar tu propio rol");
        }
        if (usuarios.existsByNombreUsuarioAndIdNot(dto.nombreUsuario(), id)) {
            throw BusinessException.conflict("El nombre de usuario ya está registrado");
        }
        u.setRol(activeRole(dto.idRol()));
        u.setNombreUsuario(dto.nombreUsuario());
        return DtoMapper.usuario(usuarios.saveAndFlush(u));
    }
    @Override @Transactional
    public UsuarioResponseDto cambiarEstado(Integer id, boolean estado, Integer actorId) {
        if (!estado && id.equals(actorId)) throw BusinessException.conflict("No puedes desactivar tu propia cuenta");
        Usuario u = find(id);
        u.setEstado(estado);
        return DtoMapper.usuario(u);
    }
    @Override @Transactional
    public void cambiarPassword(Integer id, PasswordRequest dto) {
        Passwords.validate(dto.password());
        find(id).setPasswordHash(encoder.encode(dto.password()));
    }
    private Usuario find(Integer id) {
        return usuarios.findOneById(id).orElseThrow(() -> BusinessException.missing("Usuario"));
    }
    private Rol activeRole(Integer id) {
        Rol r = roles.findById(id).orElseThrow(() -> BusinessException.missing("Rol"));
        if (!r.isEstado()) throw BusinessException.conflict("El rol está inactivo");
        return r;
    }
}
