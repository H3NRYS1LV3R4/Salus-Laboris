package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.dto.*;
import com.saluslaboris.api.entity.*;
import com.saluslaboris.api.repository.*;
import com.saluslaboris.api.service.*;
import com.saluslaboris.api.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saluslaboris.api.security.Permissions;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {
    private final RolRepository roles;
    private final PaginaRepository paginas;
    private final AccesoRepository accesos;

    @Override public PageResponse<RolResponse> listarRoles(int page, int size) {
        return PageResponse.of(roles.findAll(PageRequest.of(page, size, Sort.by("id"))).map(DtoMapper::rol));
    }
    @Override @Transactional public RolResponse crearRol(RolRequest dto) {
        if (roles.existsByNombre(dto.nombre())) throw BusinessException.conflict("El rol ya existe");
        Rol r = new Rol(); r.setNombre(dto.nombre()); r.setDescripcion(dto.descripcion());
        return DtoMapper.rol(roles.saveAndFlush(r));
    }
    @Override @Transactional public RolResponse actualizarRol(Integer id, RolRequest dto) {
        Rol r = rol(id);
        if (Permissions.ADMIN.equals(r.getNombre()) && !Permissions.ADMIN.equals(dto.nombre())) {
            throw BusinessException.conflict("El nombre ADMINISTRADOR está reservado");
        }
        if (roles.existsByNombreAndIdNot(dto.nombre(), id)) throw BusinessException.conflict("El rol ya existe");
        r.setNombre(dto.nombre()); r.setDescripcion(dto.descripcion());
        return DtoMapper.rol(roles.saveAndFlush(r));
    }
    @Override @Transactional public RolResponse estadoRol(Integer id, boolean estado) {
        Rol r = rol(id);
        if (Permissions.ADMIN.equals(r.getNombre()) && !estado) {
            throw BusinessException.conflict("No se puede desactivar el rol ADMINISTRADOR");
        }
        r.setEstado(estado); return DtoMapper.rol(r);
    }
    @Override public PageResponse<PaginaResponse> listarPaginas(int page, int size) {
        return PageResponse.of(paginas.findAll(PageRequest.of(page, size, Sort.by("id"))).map(DtoMapper::pagina));
    }
    @Override @Transactional public PaginaResponse crearPagina(PaginaRequest dto) {
        if (paginas.existsByRuta(dto.ruta())) throw BusinessException.conflict("La ruta ya existe");
        Pagina p = new Pagina(); fill(p, dto);
        return DtoMapper.pagina(paginas.saveAndFlush(p));
    }
    @Override @Transactional public PaginaResponse actualizarPagina(Integer id, PaginaRequest dto) {
        Pagina p = pagina(id);
        if (Permissions.CORE_PAGES.contains(p.getRuta()) && !p.getRuta().equals(dto.ruta())) {
            throw BusinessException.conflict("No se puede cambiar una ruta de administración reservada");
        }
        if (paginas.existsByRutaAndIdNot(dto.ruta(), id)) throw BusinessException.conflict("La ruta ya existe");
        fill(p, dto); return DtoMapper.pagina(paginas.saveAndFlush(p));
    }
    @Override @Transactional public PaginaResponse estadoPagina(Integer id, boolean estado) {
        Pagina p = pagina(id);
        if (Permissions.CORE_PAGES.contains(p.getRuta()) && !estado) {
            throw BusinessException.conflict("No se puede desactivar una página de administración reservada");
        }
        p.setEstado(estado); return DtoMapper.pagina(p);
    }
    @Override public List<PaginaResponse> obtenerAccesos(Integer idRol) {
        rol(idRol);
        return accesos.findPaginas(idRol).stream().map(DtoMapper::pagina).toList();
    }
    @Override @Transactional public List<PaginaResponse> asignarAccesos(Integer idRol, AccesoRequest dto) {
        Rol r = rol(idRol);
        if (!r.isEstado()) throw BusinessException.conflict("El rol está inactivo");
        List<Pagina> elegidas = paginas.findAllById(dto.idPaginas());
        if (elegidas.size() != dto.idPaginas().size()) throw BusinessException.missing("Página");
        if (elegidas.stream().anyMatch(p -> !p.isEstado())) throw BusinessException.conflict("Hay páginas inactivas");
        if (Permissions.ADMIN.equals(r.getNombre()) && !elegidas.stream().map(Pagina::getRuta).toList().containsAll(Permissions.CORE_PAGES)) {
            throw BusinessException.conflict("ADMINISTRADOR debe conservar las cinco páginas de administración");
        }
        List<Acceso> actuales = accesos.findByIdIdRol(idRol);
        Set<Integer> existentes = new HashSet<>();
        for (Acceso acceso : actuales) {
            Integer idPagina = acceso.getId().getIdPagina();
            existentes.add(idPagina);
            if (!dto.idPaginas().contains(idPagina)) accesos.delete(acceso);
        }
        for (Pagina p : elegidas) {
            if (!existentes.contains(p.getId())) accesos.save(new Acceso(r, p));
        }
        accesos.flush();
        return accesos.findPaginas(idRol).stream().map(DtoMapper::pagina).toList();
    }
    private Rol rol(Integer id) { return roles.findById(id).orElseThrow(() -> BusinessException.missing("Rol")); }
    private Pagina pagina(Integer id) { return paginas.findById(id).orElseThrow(() -> BusinessException.missing("Página")); }
    private void fill(Pagina p, PaginaRequest d) {
        p.setNombre(d.nombre().trim()); p.setRuta(d.ruta()); p.setIcono(d.icono());
    }
}
