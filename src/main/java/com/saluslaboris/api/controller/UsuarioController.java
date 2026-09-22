package com.saluslaboris.api.controller;

import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.security.UserPrincipal;
import com.saluslaboris.api.service.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@Validated
@PreAuthorize("hasRole('ADMINISTRADOR') and hasAuthority('PAGE:/usuarios')")
public class UsuarioController {

    private final UsuarioService usuarios;

    public UsuarioController(UsuarioService usuarios) {
        this.usuarios = usuarios;
    }

    @GetMapping
    public PageResponse<UsuarioResponseDto> listar(@RequestParam(defaultValue = "0") @Min(0) int page,
                                                   @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return usuarios.listar(page, size);
    }

    @GetMapping("/{id}")
    public UsuarioResponseDto obtener(@PathVariable @Positive Integer id) {
        return usuarios.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponseDto crear(@Valid @RequestBody UsuarioCreateDto dto) {
        return usuarios.crear(dto);
    }

    @PutMapping("/{id}")
    public UsuarioResponseDto actualizar(@PathVariable @Positive Integer id, @Valid @RequestBody UsuarioUpdateDto dto,
                                         @AuthenticationPrincipal UserPrincipal actor) {
        return usuarios.actualizar(id, dto, actor.id());
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponseDto estado(@PathVariable @Positive Integer id, @Valid @RequestBody EstadoRequest dto,
                                     @AuthenticationPrincipal UserPrincipal actor) {
        return usuarios.cambiarEstado(id, dto.estado(), actor.id());
    }

    @PutMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void password(@PathVariable @Positive Integer id, @Valid @RequestBody PasswordRequest dto) {
        usuarios.cambiarPassword(id, dto);
    }
}