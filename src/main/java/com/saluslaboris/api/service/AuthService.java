package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface AuthService {
    JwtResponse login(LoginRequest request);
    PerfilResponse perfil(Integer idUsuario);
}
