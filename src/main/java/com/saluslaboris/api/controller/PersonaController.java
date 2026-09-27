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
@RequestMapping("/api/v1/personas")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAuthority('PAGE:/personas')")
public class PersonaController {

    private final PersonaService personas;
    @GetMapping
    public PageResponse<PersonaDtos.Response> listar(@RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return personas.listar(page, size);
    }
    @GetMapping("/{id}")
    public PersonaDtos.Response obtener(@PathVariable @Positive Integer id) { return personas.obtener(id); }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaDtos.Response crear(@Valid @RequestBody PersonaDtos.Request dto) { return personas.crear(dto); }
    @PutMapping("/{id}")
    public PersonaDtos.Response actualizar(@PathVariable @Positive Integer id, @Valid @RequestBody PersonaDtos.Request dto) {
        return personas.actualizar(id, dto);
    }
    @PatchMapping("/{id}/estado")
    public PersonaDtos.Response estado(@PathVariable @Positive Integer id, @Valid @RequestBody EstadoRequest dto,
                                   @AuthenticationPrincipal UserPrincipal actor) {
        return personas.cambiarEstado(id, dto.estado(), actor.idPersona());
    }

}
