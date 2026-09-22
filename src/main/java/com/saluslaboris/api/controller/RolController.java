package com.saluslaboris.api.controller;

import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.service.CatalogoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles")
@Validated
@PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('PAGE:/roles')")
public class RolController {

    private final CatalogoService catalogo;

    public RolController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public PageResponse<RolResponse> listar(@RequestParam(defaultValue = "0") @Min(0) int page,
                                           @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return catalogo.listarRoles(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RolResponse crear(@Valid @RequestBody RolRequest dto) {
        return catalogo.crearRol(dto);
    }

    @PutMapping("/{id}")
    public RolResponse actualizar(@PathVariable @Positive Integer id, @Valid @RequestBody RolRequest dto) {
        return catalogo.actualizarRol(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public RolResponse estado(@PathVariable @Positive Integer id, @Valid @RequestBody EstadoRequest dto) {
        return catalogo.estadoRol(id, dto.estado());
    }
}