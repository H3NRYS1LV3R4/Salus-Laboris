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
@RequestMapping("/api/v1/paginas")
@Validated
@PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('PAGE:/paginas')")
public class PaginaController {

    private final CatalogoService catalogo;

    public PaginaController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public PageResponse<PaginaResponse> listar(@RequestParam(defaultValue = "0") @Min(0) int page,
                                              @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return catalogo.listarPaginas(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaginaResponse crear(@Valid @RequestBody PaginaRequest dto) {
        return catalogo.crearPagina(dto);
    }

    @PutMapping("/{id}")
    public PaginaResponse actualizar(@PathVariable @Positive Integer id, @Valid @RequestBody PaginaRequest dto) {
        return catalogo.actualizarPagina(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public PaginaResponse estado(@PathVariable @Positive Integer id, @Valid @RequestBody EstadoRequest dto) {
        return catalogo.estadoPagina(id, dto.estado());
    }
}