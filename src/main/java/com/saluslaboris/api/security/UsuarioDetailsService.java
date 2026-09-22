package com.saluslaboris.api.security;


import com.saluslaboris.api.entity.Usuario;
import com.saluslaboris.api.repository.*;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarios;
    private final AccesoRepository accesos;

    @Override
    public UserPrincipal loadUserByUsername(String username) {
        return principal(usuarios.findByNombreUsuario(username)
            .orElseThrow(() -> new UsernameNotFoundException("Credenciales inválidas")));
    }
    public UserPrincipal loadById(Integer id) {
        return principal(usuarios.findOneById(id)
            .orElseThrow(() -> new UsernameNotFoundException("Cuenta no disponible")));
    }
    private UserPrincipal principal(Usuario u) {
        var authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + u.getRol().getNombre()));
        accesos.findPaginasActivas(u.getRol().getId()).forEach(p ->
            authorities.add(new SimpleGrantedAuthority("PAGE:" + p.getRuta())));
        return new UserPrincipal(u.getId(), u.getPersona().getId(), u.getRol().getId(),
            u.getNombreUsuario(), u.getPasswordHash(),
            u.isEstado() && u.getPersona().isEstado() && u.getRol().isEstado(), authorities);
    }
}
