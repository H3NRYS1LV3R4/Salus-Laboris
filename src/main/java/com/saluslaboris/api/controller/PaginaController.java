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
@RequestMapping("/api/v1/paginas")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('PAGE:/paginas')")
public class PaginaController {

    private final CatalogoService catalogo;
    @GetMapping
    public PageResponse<PaginaResponse> listar(@RequestParam(defaultValue = "0") @Min(0) int page,
                                          @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return catalogo.listarPaginas(page, size);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaginaResponse crear(@Valid @RequestBody PaginaRequest dto) { return catalogo.crearPagina(dto); }
    @PutMapping("/{id}")
    public PaginaResponse actualizar(@PathVariable @Positive Integer id, @Valid @RequestBody PaginaRequest dto) {
        return catalogo.actualizarPagina(id, dto);
    }
    @PatchMapping("/{id}/estado")
    public PaginaResponse estado(@PathVariable @Positive Integer id, @Valid @RequestBody EstadoRequest dto) {
        return catalogo.estadoPagina(id, dto.estado());
    }

}
