package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface UsuarioService {
    PageResponse<UsuarioDtos.Response> listar(int page, int size);
    UsuarioDtos.Response obtener(Integer id);
    UsuarioDtos.Response crear(UsuarioDtos.CreateRequest request);
    UsuarioDtos.Response actualizar(Integer id, UsuarioDtos.UpdateRequest request, Integer actorId);
    UsuarioDtos.Response cambiarEstado(Integer id, boolean estado, Integer actorId);
    void cambiarPassword(Integer id, UsuarioDtos.PasswordRequest request);
}
