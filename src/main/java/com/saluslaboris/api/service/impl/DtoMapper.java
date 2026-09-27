package com.saluslaboris.api.service.impl;


import com.saluslaboris.api.entity.*;
import com.saluslaboris.api.dto.*;

final class DtoMapper {
    private DtoMapper() {}
    static PersonaDtos.Response persona(Persona p) {
        return new PersonaDtos.Response(p.getId(), p.getTipoDocumento(), p.getNroDocumento(),
            p.getNombres(), p.getApellidoPaterno(), p.getApellidoMaterno(), p.getFechaNacimiento(),
            p.getCorreo(), p.getTelefono(), p.isEstado());
    }
    static RolDto rol(Rol r) {
        return new RolDto(r.getId(), r.getNombre(), r.getDescripcion(), r.isEstado());
    }
    static PaginaDto pagina(Pagina p) {
        return new PaginaDto(p.getId(), p.getNombre(), p.getRuta(), p.getIcono(), p.isEstado());
    }
    static UsuarioDtos.Response usuario(Usuario u) {
        return new UsuarioDtos.Response(u.getId(), persona(u.getPersona()), rol(u.getRol()),
            u.getNombreUsuario(), u.isEstado(), u.getFechaRegistro());
    }
}
