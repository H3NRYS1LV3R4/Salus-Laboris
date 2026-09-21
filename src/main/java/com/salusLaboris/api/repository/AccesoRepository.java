package com.salusLaboris.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salusLaboris.api.entity.Acceso;
import com.salusLaboris.api.entity.AccesoId;

public interface AccesoRepository extends JpaRepository<Acceso, AccesoId> {

    List<Acceso> findByRolIdRol(Integer idRol);
}