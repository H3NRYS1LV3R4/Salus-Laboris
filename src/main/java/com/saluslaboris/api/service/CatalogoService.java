package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface CatalogoService {
    PageResponse<RolDto> listarRoles(int page, int size);
    RolDto crearRol(RolDto request);
    RolDto actualizarRol(Integer id, RolDto request);
    RolDto estadoRol(Integer id, boolean estado);
    PageResponse<PaginaDto> listarPaginas(int page, int size);
    PaginaDto crearPagina(PaginaDto request);
    PaginaDto actualizarPagina(Integer id, PaginaDto request);
    PaginaDto estadoPagina(Integer id, boolean estado);
    java.util.List<PaginaDto> obtenerAccesos(Integer idRol);
    java.util.List<PaginaDto> asignarAccesos(Integer idRol, AccesoRequest request);
}
