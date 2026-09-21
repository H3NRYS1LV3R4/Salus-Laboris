package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface UsuarioService {
    PageResponse<UsuarioResponseDto> listar(int page, int size);
    UsuarioResponseDto obtener(Integer id);
    UsuarioResponseDto crear(UsuarioCreateDto request);
    UsuarioResponseDto actualizar(Integer id, UsuarioUpdateDto request, Integer actorId);
    UsuarioResponseDto cambiarEstado(Integer id, boolean estado, Integer actorId);
    void cambiarPassword(Integer id, PasswordRequest request);
}
