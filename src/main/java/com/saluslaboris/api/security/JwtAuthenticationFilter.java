package com.saluslaboris.api.security;


import io.jsonwebtoken.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final UsuarioDetailsService users;
    private final RestSecurityHandler handler;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return ("POST".equals(request.getMethod()) && "/api/v1/auth/login".equals(request.getServletPath()))
            || "OPTIONS".equals(request.getMethod());
    }
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String header = req.getHeader("Authorization");
        if (header != null) {
            try {
                if (!header.regionMatches(true, 0, "Bearer ", 0, 7) || header.length() <= 7) {
                    throw new BadCredentialsException("Token inválido");
                }
                Claims claims = jwt.parse(header.substring(7));
                int id = Integer.parseInt(claims.getSubject());
                if (id <= 0) throw new BadCredentialsException("Token inválido");
                UserPrincipal user = users.loadById(id);
                if (!user.isEnabled() || !jwt.matchesCredentials(claims, user)) {
                    throw new BadCredentialsException("Cuenta inactiva o credenciales modificadas");
                }
                var auth = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);
            } catch (JwtException | IllegalArgumentException | AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                handler.commence(req, res, new BadCredentialsException("Token inválido"));
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
