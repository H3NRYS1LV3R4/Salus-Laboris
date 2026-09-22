package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.entity.*;
import com.saluslaboris.api.repository.*;
import com.saluslaboris.api.service.*;
import com.saluslaboris.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonaServiceImpl implements PersonaService {
    private final PersonaRepository personas;
    @Override public PageResponse<PersonaResponse> listar(int page, int size) {
        return PageResponse.of(personas.findAll(PageRequest.of(page, size, Sort.by("id"))).map(DtoMapper::persona));
    }
    @Override public PersonaResponse obtener(Integer id) { return DtoMapper.persona(find(id)); }
    @Override @Transactional
    public PersonaResponse crear(PersonaRequest dto) {
        if (personas.existsByNroDocumento(dto.nroDocumento().trim())) {
            throw BusinessException.conflict("Ya existe una persona con ese documento");
        }
        Persona p = new Persona();
        fill(p, dto);
        return DtoMapper.persona(personas.saveAndFlush(p));
    }
    @Override @Transactional
    public PersonaResponse actualizar(Integer id, PersonaRequest dto) {
        Persona p = find(id);
        if (personas.existsByNroDocumentoAndIdNot(dto.nroDocumento().trim(), id)) {
            throw BusinessException.conflict("Ya existe una persona con ese documento");
        }
        fill(p, dto);
        return DtoMapper.persona(personas.saveAndFlush(p));
    }
    @Override @Transactional
    public PersonaResponse cambiarEstado(Integer id, boolean estado, Integer actorPersona) {
        if (!estado && id.equals(actorPersona)) {
            throw BusinessException.conflict("No puedes desactivar tu propia persona");
        }
        Persona p = find(id);
        p.setEstado(estado);
        return DtoMapper.persona(p);
    }
    private Persona find(Integer id) {
        return personas.findById(id).orElseThrow(() -> BusinessException.missing("Persona"));
    }
    private void fill(Persona p, PersonaRequest dto) {
        p.setTipoDocumento(dto.tipoDocumento().trim());
        p.setNroDocumento(dto.nroDocumento().trim());
        p.setNombres(dto.nombres().trim());
        p.setApellidoPaterno(dto.apellidoPaterno().trim());
        p.setApellidoMaterno(clean(dto.apellidoMaterno()));
        p.setFechaNacimiento(dto.fechaNacimiento());
        p.setCorreo(clean(dto.correo()));
        p.setTelefono(clean(dto.telefono()));
    }
    private String clean(String s) { return s == null || s.isBlank() ? null : s.trim(); }
}
