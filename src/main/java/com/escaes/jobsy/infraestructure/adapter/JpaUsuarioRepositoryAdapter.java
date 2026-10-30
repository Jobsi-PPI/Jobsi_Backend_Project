package com.escaes.jobsy.infraestructure.adapter;

import com.escaes.jobsy.domain.model.Pagina;
import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.UsuarioRepository;
import com.escaes.jobsy.infraestructure.mapper.GeneroMapper;
import com.escaes.jobsy.infraestructure.mapper.RolMapper;
import com.escaes.jobsy.infraestructure.persistence.entity.UsuarioEntity;
import com.escaes.jobsy.infraestructure.persistence.specification.UserSpecification;
import com.escaes.jobsy.infraestructure.jpa.SpringDataUsuarioRepository;
import com.escaes.jobsy.infraestructure.mapper.UsuarioMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaUsuarioRepositoryAdapter implements UsuarioRepository {

    private final SpringDataUsuarioRepository springDataUsuarioRepository;

    public JpaUsuarioRepositoryAdapter(SpringDataUsuarioRepository springDataUsuarioRepository) {
        this.springDataUsuarioRepository = springDataUsuarioRepository;
    }

    /*
     * Si el usuario ya existe solo se actualizan sus campos propios (datos, clave, bloqueo,
     * valoración, género y rol). No se reconstruyen las listas de trabajos para evitar que la
     * cascada sobrescriba trabajos o usuarios relacionados.
     */
    @Override
    public void save(Usuario usuario) {
        UsuarioEntity entity = springDataUsuarioRepository.findById(usuario.documento())
                .map(existente -> actualizarCampos(existente, usuario))
                .orElseGet(() -> UsuarioMapper.toEntity(usuario));
        springDataUsuarioRepository.save(entity);
    }

    private UsuarioEntity actualizarCampos(UsuarioEntity entity, Usuario usuario) {
        entity.setNombre(usuario.nombre());
        entity.setPrimerApellido(usuario.primerApellido());
        entity.setSegundoApellido(usuario.segundoApellido());
        entity.setCorreo(usuario.correo());
        entity.setClave(usuario.clave());
        entity.setTelefono(usuario.telefono());
        entity.setBloqueado(usuario.bloqueado());
        entity.setFechaNacimiento(usuario.fechaNacimiento());
        entity.setValoracionConteo(usuario.valoracionConteo());
        entity.setValoracionPromedio(usuario.valoracionPromedio());
        entity.setGenero(GeneroMapper.toEntity(usuario.genero()));
        entity.setRol(RolMapper.toEntity(usuario.rol()));
        return entity;
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        Optional<UsuarioEntity> entityOptional = springDataUsuarioRepository.findById(id);
        if (entityOptional.isPresent()) {
            UsuarioEntity entity = entityOptional.get();
            return Optional.of(UsuarioMapper.toDomainBasic(entity));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> findByDocumentoOrCorreoOrTelefono(Long documento, String correo, String telefono) {
        return springDataUsuarioRepository.findByDocumentoOrCorreoOrTelefono(documento, correo, telefono)
                .map(UsuarioMapper::toDomainBasic);
    }

    @Override
    public Optional<Usuario> findByCorreo(String correo) {

        return springDataUsuarioRepository.findByCorreo(correo)
                .map(UsuarioMapper::toDomainBasic);
    }

    @Override
    public Usuario findByCorreoAndClave(String correo, String clave) {
        return null;
    }

    @Override
    public Usuario findByCorreoAndClaveAndBloqueado(String correo, String clave, Boolean bloqueado) {
        return null;
    }

    @Override
    public Usuario findByCorreoAndBloqueado(String correo, Boolean bloqueado) {
        return null;
    }

    @Override
    public List<Usuario> findAll() {
        return springDataUsuarioRepository.findAll().stream()
                .map(UsuarioMapper::toDomainBasic)
                .toList();
    }

    @Override
    public List<Usuario> findAllByBloqueado(Boolean bloqueado) {
        return springDataUsuarioRepository.findAllByBloqueado(bloqueado).stream()
                .map(UsuarioMapper::toDomainBasic)
                .toList();
    }

    @Override
    public List<Usuario> findAllByGenero(String genero) {
        return List.of();
    }

    @Override
    public List<Usuario> findAllByRol(String rol) {
        return List.of();
    }

    @Override
    public List<Usuario> findAllByFechaNacimientoBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return List.of();
    }

    @Override
    public Pagina<Usuario> findUsersCriteria(Long documento, String correo, String genero, String rol,
            Boolean bloqueado, Integer valoracionConteo, Double valoracionPromedio, int size, int page) {

        Specification<UsuarioEntity> spec = UserSpecification.hasDocument(documento)
                .and(UserSpecification.hasEmail(correo))
                .and(UserSpecification.hasGender(genero))
                .and(UserSpecification.hasRol(rol))
                .and(UserSpecification.hasBloqueado(bloqueado))
                .and(UserSpecification.valoracionConteo(valoracionConteo))
                .and(UserSpecification.valoracionPromedio(valoracionPromedio));

        Pageable pageable = PageRequest.of(page, size, Sort.by("documento"));

        Page<UsuarioEntity> resultado = springDataUsuarioRepository.findAll(spec, pageable);

        return new Pagina<>(
                resultado.getContent().stream().map(UsuarioMapper::toDomainBasic).toList(),
                resultado.getNumber(),
                resultado.getSize(),
                resultado.getTotalElements(),
                resultado.getTotalPages());
    }

    @Override
    public long count() {
        return springDataUsuarioRepository.count();
    }

    @Override
    public long countByBloqueado(Boolean bloqueado) {
        return springDataUsuarioRepository.countByBloqueado(bloqueado);
    }

    @Override
    public boolean existsByRol(String rol) {
        return springDataUsuarioRepository.existsByRolNombre(rol);
    }

    @Override
    public void deleteById(Long documento) {
        springDataUsuarioRepository.deleteById(documento);
    }

    @Override
    public void deleteByCorreo(String correo) {
        springDataUsuarioRepository.findByCorreo(correo)
                .ifPresent(springDataUsuarioRepository::delete);
    }

    @Override
    public void delete(Usuario usuario) {
        springDataUsuarioRepository.deleteById(usuario.documento());
    }

    @Override
    public Optional<Usuario> findByTelefono(String telefono) {
        return springDataUsuarioRepository
                .findByTelefono(telefono).map(UsuarioMapper::toDomainBasic);
    }
}
