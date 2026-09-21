package com.saluslaboris.api.controller;

import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.service.*;
import com.saluslaboris.api.security.UserPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('PAGE:/accesos')")
public class AccesoController {

    private final CatalogoService catalogo;
    @GetMapping("/roles/{idRol}")
    public java.util.List<PaginaResponse> obtener(@PathVariable @Positive Integer idRol) {
        return catalogo.obtenerAccesos(idRol);
    }
    @PutMapping("/roles/{idRol}")
    public java.util.List<PaginaResponse> asignar(@PathVariable @Positive Integer idRol,
                                                @Valid @RequestBody AccesoRequest dto) {
        return catalogo.asignarAccesos(idRol, dto);
    }

}
