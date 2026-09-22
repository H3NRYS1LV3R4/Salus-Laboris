package com.saluslaboris.api.repository;

import com.saluslaboris.api.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {
    boolean existsByNroDocumento(String nroDocumento);
    boolean existsByNroDocumentoAndIdNot(String nroDocumento, Integer id);
}