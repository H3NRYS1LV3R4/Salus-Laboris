package com.saluslaboris.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public final class AuthDtos {

    private AuthDtos() {
    }

    public record LoginRequest(
        @NotBlank @Size(max = 50) String nombreUsuario,
        @NotBlank @Size(max = 72) String password
    ) {
        @Override
        public String toString() {
            return "LoginRequest[credenciales ocultas]";
        }
    }

    public record JwtResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UsuarioDtos.Response usuario,
        List<PaginaDto> paginas
    ) {
    }

    public record PerfilResponse(
        UsuarioDtos.Response usuario,
        List<PaginaDto> paginas
    ) {
    }
}
