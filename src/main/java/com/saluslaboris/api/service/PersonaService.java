package com.saluslaboris.api.service;

import com.saluslaboris.api.dto.*;

public interface PersonaService {
    PageResponse<PersonaDtos.Response> listar(int page, int size);
    PersonaDtos.Response obtener(Integer id);
    PersonaDtos.Response crear(PersonaDtos.Request request);
    PersonaDtos.Response actualizar(Integer id, PersonaDtos.Request request);
    PersonaDtos.Response cambiarEstado(Integer id, boolean estado, Integer actorPersona);
}
