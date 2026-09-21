package com.saluslaboris.api.repository;

import com.saluslaboris.api.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PaginaRepository extends JpaRepository<Pagina, Integer> {
    Optional<Pagina> findByRuta(String ruta);
    boolean existsByRuta(String ruta);
    boolean existsByRutaAndIdNot(String ruta, Integer id);
}
