package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.entity.Usuario;
import com.saluslaboris.api.exception.BusinessException;
import com.saluslaboris.api.repository.*;
import com.saluslaboris.api.security.*;
import com.saluslaboris.api.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwt;
    private final UsuarioRepository usuarios;
    private final AccesoRepository accesos;

    public AuthServiceImpl(AuthenticationManager authenticationManager, JwtService jwt,
                           UsuarioRepository usuarios, AccesoRepository accesos) {
        this.authenticationManager = authenticationManager;
        this.jwt = jwt;
        this.usuarios = usuarios;
        this.accesos = accesos;
    }

    @Override
    public JwtResponse login(LoginRequest dto) {
        var auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(dto.nombreUsuario(), dto.password()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        PerfilResponse perfil = perfil(principal.id());
        return new JwtResponse(jwt.issue(principal), "Bearer", jwt.expiresInSeconds(), perfil.usuario(), perfil.paginas());
    }

    @Override
    public PerfilResponse perfil(Integer idUsuario) {
        Usuario u = usuarios.findById(idUsuario)
            .orElseThrow(() -> BusinessException.missing("Usuario con ID " + idUsuario));
        return new PerfilResponse(
            DtoMapper.usuario(u),
            accesos.findPaginasActivas(u.getRol().getId()).stream().map(DtoMapper::pagina).toList()
        );
    }
}