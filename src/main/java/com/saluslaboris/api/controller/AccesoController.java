package com.saluslaboris.api.controller;

import com.saluslaboris.api.dto.AccesoRequest;
import com.saluslaboris.api.dto.PaginaResponse;
import com.saluslaboris.api.service.CatalogoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accesos")
@Validated
@PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('PAGE:/accesos')")
public class AccesoController {

    private final CatalogoService catalogo;

    public AccesoController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping("/roles/{idRol}")
    public List<PaginaResponse> obtener(@PathVariable @Positive Integer idRol) {
        return catalogo.obtenerAccesos(idRol);
    }

    @PutMapping("/roles/{idRol}")
    public List<PaginaResponse> asignar(@PathVariable @Positive Integer idRol,
                                        @Valid @RequestBody AccesoRequest dto) {
        return catalogo.asignarAccesos(idRol, dto);
    }
}