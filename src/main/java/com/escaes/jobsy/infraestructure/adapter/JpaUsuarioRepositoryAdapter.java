package com.escaes.jobsy.infraestructure.adapter;

import com.escaes.jobsy.domain.model.Usuario;
import com.escaes.jobsy.domain.repository.UsuarioRepository;
import com.escaes.jobsy.infraestructure.entity.UsuarioEntity;
import com.escaes.jobsy.infraestructure.jpa.SpringDataUsuarioRepository;
import com.escaes.jobsy.infraestructure.mapper.UsuarioMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUsuarioRepositoryAdapter implements UsuarioRepository {

    private final SpringDataUsuarioRepository springDataUsuarioRepository;

    public JpaUsuarioRepositoryAdapter(SpringDataUsuarioRepository springDataUsuarioRepository) {
        this.springDataUsuarioRepository = springDataUsuarioRepository;
    }

    @Override
    public void save(Usuario usuario) {
        UsuarioEntity entity = UsuarioMapper.toEntity(usuario);
        springDataUsuarioRepository.save(entity);
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        Optional<UsuarioEntity> entityOptional = springDataUsuarioRepository.findById(id);
        if (entityOptional.isPresent()) {
            UsuarioEntity entity = entityOptional.get();
            return Optional.of(UsuarioMapper.toDomain(entity));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Usuario> findByDocumento(Integer documento) {
        return springDataUsuarioRepository.findByDocumento(documento)
                .map(UsuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByCorreo(String correo) {

        return springDataUsuarioRepository.findByCorreo(correo)
                .map(UsuarioMapper::toDomain);
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
        return List.of();
    }

    @Override
    public List<Usuario> findAllByBloqueado(Boolean bloqueado) {
        return List.of();
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
    public List<Usuario> findAllByFechaNacimientoBetween(String fechaInicio, String fechaFin) {
        return List.of();
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteByDocumento(Integer documento) {

    }

    @Override
    public void deleteByCorreo(String correo) {

    }

    @Override
    public void delete(Usuario usuario) {

    }

    @Override
    public Optional<Usuario> findByTelefono(String telefono) {
        return springDataUsuarioRepository
                .findByTelefono(telefono).map(UsuarioMapper::toDomain);
    }
}
