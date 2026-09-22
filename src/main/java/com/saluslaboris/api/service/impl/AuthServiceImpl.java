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

import com.saluslaboris.api.security.*;
import org.springframework.security.authentication.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    private final UsuarioRepository usuarios;
    private final AccesoRepository accesos;
    @Override public JwtResponse login(LoginRequest dto) {
        var auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(dto.nombreUsuario(), dto.password()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        PerfilResponse perfil = perfil(principal.id());
        return new JwtResponse(jwt.issue(principal), "Bearer", jwt.expiresInSeconds(), perfil.usuario(), perfil.paginas());
    }
    @Override public PerfilResponse perfil(Integer idUsuario) {
        Usuario u = usuarios.findOneById(idUsuario).orElseThrow(() -> BusinessException.missing("Usuario"));
        return new PerfilResponse(DtoMapper.usuario(u),
            accesos.findPaginasActivas(u.getRol().getId()).stream().map(DtoMapper::pagina).toList());
    }
}
