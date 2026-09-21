package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface PersonaService {
    PageResponse<PersonaResponse> listar(int page, int size);
    PersonaResponse obtener(Integer id);
    PersonaResponse crear(PersonaRequest request);
    PersonaResponse actualizar(Integer id, PersonaRequest request);
    PersonaResponse cambiarEstado(Integer id, boolean estado, Integer actorPersona);
}
