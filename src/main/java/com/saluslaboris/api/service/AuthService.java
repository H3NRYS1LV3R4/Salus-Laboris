package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface AuthService {
    AuthDtos.JwtResponse login(AuthDtos.LoginRequest request);
    AuthDtos.PerfilResponse perfil(Integer idUsuario);
}
