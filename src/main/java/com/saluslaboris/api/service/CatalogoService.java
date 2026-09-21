package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface CatalogoService {
    PageResponse<RolResponse> listarRoles(int page, int size);
    RolResponse crearRol(RolRequest request);
    RolResponse actualizarRol(Integer id, RolRequest request);
    RolResponse estadoRol(Integer id, boolean estado);
    PageResponse<PaginaResponse> listarPaginas(int page, int size);
    PaginaResponse crearPagina(PaginaRequest request);
    PaginaResponse actualizarPagina(Integer id, PaginaRequest request);
    PaginaResponse estadoPagina(Integer id, boolean estado);
    java.util.List<PaginaResponse> obtenerAccesos(Integer idRol);
    java.util.List<PaginaResponse> asignarAccesos(Integer idRol, AccesoRequest request);
}
