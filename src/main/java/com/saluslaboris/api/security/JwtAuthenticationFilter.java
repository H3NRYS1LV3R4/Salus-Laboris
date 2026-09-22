package com.saluslaboris.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UsuarioDetailsService users;
    private final RestSecurityHandler handler;

    public JwtAuthenticationFilter(JwtService jwt, UsuarioDetailsService users, RestSecurityHandler handler) {
        this.jwt = jwt;
        this.users = users;
        this.handler = handler;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return ("POST".equalsIgnoreCase(request.getMethod()) && "/api/v1/auth/login".equals(request.getServletPath()))
            || "OPTIONS".equalsIgnoreCase(request.getMethod());
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

                String token = header.substring(7).trim();
                Claims claims = jwt.parse(token);

                // El ID se extrae del claim "id" que asignó JwtService
                Integer id = claims.get("id", Integer.class);
                if (id == null || id <= 0) {
                    throw new BadCredentialsException("Token inválido");
                }

                UserPrincipal user = users.loadById(id);

                if (!user.isEnabled()) {
                    throw new BadCredentialsException("Cuenta inactiva");
                }

                var auth = UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities());
                var context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(auth);
                SecurityContextHolder.setContext(context);

            } catch (JwtException | IllegalArgumentException | AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                handler.commence(req, res, new BadCredentialsException("Token inválido o expirado"));
                return;
            }
        }

        chain.doFilter(req, res);
    }
}