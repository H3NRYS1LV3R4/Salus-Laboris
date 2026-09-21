package com.saluslaboris.api.repository;

import com.saluslaboris.api.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    @EntityGraph(attributePaths = {"persona", "rol"})
    Optional<Usuario> findByNombreUsuario(String nombreUsuario);
    @EntityGraph(attributePaths = {"persona", "rol"})
    Optional<Usuario> findOneById(Integer id);
    boolean existsByNombreUsuario(String nombreUsuario);
    boolean existsByNombreUsuarioAndIdNot(String nombreUsuario, Integer id);
    boolean existsByPersonaId(Integer idPersona);
}
