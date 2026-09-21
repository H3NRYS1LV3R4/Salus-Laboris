package com.saluslaboris.api.service.impl;


import com.saluslaboris.api.entity.*;
import com.saluslaboris.api.dto.*;

final class DtoMapper {
    private DtoMapper() {}
    static PersonaResponse persona(Persona p) {
        return new PersonaResponse(p.getId(), p.getTipoDocumento(), p.getNroDocumento(),
            p.getNombres(), p.getApellidoPaterno(), p.getApellidoMaterno(), p.getFechaNacimiento(),
            p.getCorreo(), p.getTelefono(), p.isEstado());
    }
    static RolResponse rol(Rol r) {
        return new RolResponse(r.getId(), r.getNombre(), r.getDescripcion(), r.isEstado());
    }
    static PaginaResponse pagina(Pagina p) {
        return new PaginaResponse(p.getId(), p.getNombre(), p.getRuta(), p.getIcono(), p.isEstado());
    }
    static UsuarioResponseDto usuario(Usuario u) {
        return new UsuarioResponseDto(u.getId(), persona(u.getPersona()), rol(u.getRol()),
            u.getNombreUsuario(), u.isEstado(), u.getFechaRegistro());
    }
}
