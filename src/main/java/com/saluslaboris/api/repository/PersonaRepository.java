package com.saluslaboris.api.repository;

import com.saluslaboris.api.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {
    boolean existsByNroDocumento(String nroDocumento);
    boolean existsByNroDocumentoAndIdNot(String nroDocumento, Integer id);
}
