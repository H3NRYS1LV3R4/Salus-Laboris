package com.saluslaboris.api.repository;

import com.saluslaboris.api.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface AccesoRepository extends JpaRepository<Acceso, AccesoId> {
    @Query("select a.pagina from Acceso a where a.rol.id = :idRol and a.rol.estado = true and a.pagina.estado = true order by a.pagina.id")
    List<Pagina> findPaginasActivas(@Param("idRol") Integer idRol);
    @Query("select a.pagina from Acceso a where a.rol.id = :idRol order by a.pagina.id")
    List<Pagina> findPaginas(@Param("idRol") Integer idRol);
    List<Acceso> findByIdIdRol(Integer idRol);
}
