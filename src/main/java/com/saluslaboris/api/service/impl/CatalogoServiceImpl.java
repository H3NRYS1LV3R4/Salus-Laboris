package com.saluslaboris.api.service.impl;

import com.saluslaboris.api.dto.AccesoRequest;
import com.saluslaboris.api.dto.PageResponse;
import com.saluslaboris.api.dto.PaginaRequest;
import com.saluslaboris.api.dto.PaginaResponse;
import com.saluslaboris.api.dto.RolRequest;
import com.saluslaboris.api.dto.RolResponse;
import com.saluslaboris.api.entity.Acceso;
import com.saluslaboris.api.entity.Pagina;
import com.saluslaboris.api.entity.Rol;
import com.saluslaboris.api.exception.BusinessException;
import com.saluslaboris.api.repository.AccesoRepository;
import com.saluslaboris.api.repository.PaginaRepository;
import com.saluslaboris.api.repository.RolRepository;
import com.saluslaboris.api.security.Permissions;
import com.saluslaboris.api.service.CatalogoService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {

    private final RolRepository roles;
    private final PaginaRepository paginas;
    private final AccesoRepository accesos;

    public CatalogoServiceImpl(RolRepository roles, PaginaRepository paginas, AccesoRepository accesos) {
        this.roles = roles;
        this.paginas = paginas;
        this.accesos = accesos;
    }

    @Override
    public PageResponse<RolResponse> listarRoles(int page, int size) {
        var consulta = roles.findAll(PageRequest.of(page, size, Sort.by("id")));
        return PageResponse.of(consulta.map(DtoMapper::rol));
    }

    @Override
    @Transactional
    public RolResponse crearRol(RolRequest dto) {
        if (roles.existsByNombre(dto.nombre())) {
            throw BusinessException.conflict("El rol ya se encuentra registrado");
        }
        Rol r = new Rol();
        r.setNombre(dto.nombre().toUpperCase());
        r.setDescripcion(dto.descripcion());
        return DtoMapper.rol(roles.save(r));
    }

    @Override
    @Transactional
    public RolResponse actualizarRol(Integer id, RolRequest dto) {
        Rol r = rol(id);
        String nombreNormalizado = dto.nombre().toUpperCase();

        if (Permissions.ADMIN.equalsIgnoreCase(r.getNombre()) && !Permissions.ADMIN.equalsIgnoreCase(nombreNormalizado)) {
            throw BusinessException.conflict("El nombre de rol ADMINISTRADOR no puede ser modificado");
        }
        if (roles.existsByNombreAndIdNot(nombreNormalizado, id)) {
            throw BusinessException.conflict("Ya existe otro rol con ese nombre");
        }

        r.setNombre(nombreNormalizado);
        r.setDescripcion(dto.descripcion());
        return DtoMapper.rol(roles.save(r));
    }

    @Override
    @Transactional
    public RolResponse estadoRol(Integer id, boolean estado) {
        Rol r = rol(id);
        if (Permissions.ADMIN.equalsIgnoreCase(r.getNombre()) && !estado) {
            throw BusinessException.conflict("No se puede desactivar el rol de ADMINISTRADOR");
        }
        r.setEstado(estado);
        return DtoMapper.rol(roles.save(r));
    }

    @Override
    public PageResponse<PaginaResponse> listarPaginas(int page, int size) {
        var consulta = paginas.findAll(PageRequest.of(page, size, Sort.by("id")));
        return PageResponse.of(consulta.map(DtoMapper::pagina));
    }

    @Override
    @Transactional
    public PaginaResponse crearPagina(PaginaRequest dto) {
        if (paginas.existsByRuta(dto.ruta())) {
            throw BusinessException.conflict("La ruta de acceso ya se encuentra registrada");
        }
        Pagina p = new Pagina();
        p.setNombre(dto.nombre());
        p.setRuta(dto.ruta());
        p.setIcono(dto.icono());
        return DtoMapper.pagina(paginas.save(p));
    }

    @Override
    @Transactional
    public PaginaResponse actualizarPagina(Integer id, PaginaRequest dto) {
        Pagina p = pagina(id);
        if (Permissions.CORE_PAGES.contains(p.getRuta()) && !p.getRuta().equals(dto.ruta())) {
            throw BusinessException.conflict("No se puede modificar una ruta de administración del sistema");
        }
        if (paginas.existsByRutaAndIdNot(dto.ruta(), id)) {
            throw BusinessException.conflict("Ya existe otra página registrada con esa ruta");
        }

        p.setNombre(dto.nombre());
        p.setRuta(dto.ruta());
        p.setIcono(dto.icono());
        return DtoMapper.pagina(paginas.save(p));
    }

    @Override
    @Transactional
    public PaginaResponse estadoPagina(Integer id, boolean estado) {
        Pagina p = pagina(id);
        if (Permissions.CORE_PAGES.contains(p.getRuta()) && !estado) {
            throw BusinessException.conflict("No se puede desactivar una página de administración reservada");
        }
        p.setEstado(estado);
        return DtoMapper.pagina(paginas.save(p));
    }

    @Override
    public List<PaginaResponse> obtenerAccesos(Integer idRol) {
        rol(idRol);
        return accesos.findPaginas(idRol).stream().map(DtoMapper::pagina).toList();
    }

    @Override
    @Transactional
    public List<PaginaResponse> asignarAccesos(Integer idRol, AccesoRequest dto) {
        Rol r = rol(idRol);
        if (!r.isEstado()) {
            throw BusinessException.conflict("No se pueden asignar accesos a un rol inactivo");
        }

        List<Pagina> paginasSeleccionadas = paginas.findAllById(dto.idPaginas());
        if (paginasSeleccionadas.size() != dto.idPaginas().size()) {
            throw BusinessException.missing("Una o más páginas seleccionadas no existen");
        }
        if (paginasSeleccionadas.stream().anyMatch(p -> !p.isEstado())) {
            throw BusinessException.conflict("No se pueden asociar páginas inactivas");
        }
        if (Permissions.ADMIN.equalsIgnoreCase(r.getNombre()) 
            && !paginasSeleccionadas.stream().map(Pagina::getRuta).toList().containsAll(Permissions.CORE_PAGES)) {
            throw BusinessException.conflict("El rol ADMINISTRADOR debe conservar todas las páginas maestras");
        }

        List<Acceso> actuales = accesos.findByIdIdRol(idRol);
        Set<Integer> nuevasIds = new HashSet<>(dto.idPaginas());

        // Eliminar accesos que ya no están en la lista seleccionada
        List<Acceso> paraEliminar = actuales.stream()
            .filter(a -> !nuevasIds.contains(a.getId().getIdPagina()))
            .toList();
        if (!paraEliminar.isEmpty()) {
            accesos.deleteAll(paraEliminar);
        }

        // Agregar únicamente los nuevos accesos
        Set<Integer> existentesIds = new HashSet<>();
        actuales.forEach(a -> existentesIds.add(a.getId().getIdPagina()));

        List<Acceso> paraGuardar = paginasSeleccionadas.stream()
            .filter(p -> !existentesIds.contains(p.getId()))
            .map(p -> new Acceso(r, p))
            .toList();
        if (!paraGuardar.isEmpty()) {
            accesos.saveAll(paraGuardar);
        }

        return accesos.findPaginas(idRol).stream().map(DtoMapper::pagina).toList();
    }

    private Rol rol(Integer id) {
        return roles.findById(id).orElseThrow(() -> BusinessException.missing("Rol"));
    }

    private Pagina pagina(Integer id) {
        return paginas.findById(id).orElseThrow(() -> BusinessException.missing("Página"));
    }
}