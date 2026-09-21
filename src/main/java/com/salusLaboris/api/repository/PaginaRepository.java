package com.salusLaboris.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salusLaboris.api.entity.Pagina;

public interface PaginaRepository extends JpaRepository<Pagina, Integer> {

    Optional<Pagina> findByRuta(String ruta);
}