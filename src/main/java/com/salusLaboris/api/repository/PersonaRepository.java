package com.salusLaboris.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salusLaboris.api.entity.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    Optional<Persona> findByNroDocumento(String nroDocumento);

    boolean existsByNroDocumento(String nroDocumento);
}