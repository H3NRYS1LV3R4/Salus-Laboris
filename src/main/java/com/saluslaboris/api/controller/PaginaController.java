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
@PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRADOR')")
public class PaginaController {

    private final CatalogoService catalogo;
    @GetMapping
    public PageResponse<PaginaDto> listar(@RequestParam(defaultValue = "0") @Min(0) int page,
                                          @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return catalogo.listarPaginas(page, size);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaginaDto crear(@Valid @RequestBody PaginaDto dto) { return catalogo.crearPagina(dto); }
    @PutMapping("/{id}")
    public PaginaDto actualizar(@PathVariable @Positive Integer id, @Valid @RequestBody PaginaDto dto) {
        return catalogo.actualizarPagina(id, dto);
    }
    @PatchMapping("/{id}/estado")
    public PaginaDto estado(@PathVariable @Positive Integer id, @Valid @RequestBody EstadoRequest dto) {
        return catalogo.estadoPagina(id, dto.estado());
    }

}
