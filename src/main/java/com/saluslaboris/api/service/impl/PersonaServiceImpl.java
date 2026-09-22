package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.dto.PageResponse;
import com.saluslaboris.api.dto.PersonaRequest;
import com.saluslaboris.api.dto.PersonaResponse;
import com.saluslaboris.api.entity.Persona;
import com.saluslaboris.api.exception.BusinessException;
import com.saluslaboris.api.repository.PersonaRepository;
import com.saluslaboris.api.service.PersonaService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personas;

    public PersonaServiceImpl(PersonaRepository personas) {
        this.personas = personas;
    }

    @Override
    public PageResponse<PersonaResponse> listar(int page, int size) {
        var consulta = personas.findAll(PageRequest.of(page, size, Sort.by("id")));
        return PageResponse.of(consulta.map(DtoMapper::persona));
    }

    @Override
    public PersonaResponse obtener(Integer id) {
        return DtoMapper.persona(find(id));
    }

    @Override
    @Transactional
    public PersonaResponse crear(PersonaRequest dto) {
        if (personas.existsByNroDocumento(dto.nroDocumento())) {
            throw BusinessException.conflict("Ya existe una persona registrada con ese número de documento");
        }

        Persona p = new Persona();
        mapearDatos(p, dto);
        return DtoMapper.persona(personas.save(p));
    }

    @Override
    @Transactional
    public PersonaResponse actualizar(Integer id, PersonaRequest dto) {
        Persona p = find(id);
        if (personas.existsByNroDocumentoAndIdNot(dto.nroDocumento(), id)) {
            throw BusinessException.conflict("Ya existe otra persona registrada con ese número de documento");
        }

        mapearDatos(p, dto);
        return DtoMapper.persona(personas.save(p));
    }

    @Override
    @Transactional
    public PersonaResponse cambiarEstado(Integer id, boolean estado, Integer actorPersonaId) {
        if (!estado && id.equals(actorPersonaId)) {
            throw BusinessException.conflict("No puedes desactivar tu propio registro de persona");
        }

        Persona p = find(id);
        p.setEstado(estado);
        return DtoMapper.persona(personas.save(p));
    }

    private Persona find(Integer id) {
        return personas.findById(id)
            .orElseThrow(() -> BusinessException.missing("Persona"));
    }

    private void mapearDatos(Persona p, PersonaRequest dto) {
        p.setTipoDocumento(dto.tipoDocumento());
        p.setNroDocumento(dto.nroDocumento());
        p.setNombres(dto.nombres());
        p.setApellidoPaterno(dto.apellidoPaterno());
        p.setApellidoMaterno(dto.apellidoMaterno());
        p.setFechaNacimiento(dto.fechaNacimiento());
        p.setCorreo(dto.correo());
        p.setTelefono(dto.telefono());
    }
}