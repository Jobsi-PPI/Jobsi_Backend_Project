package com.escaes.jobsy.infraestructure.adapter;

import com.escaes.jobsy.domain.model.Rol;
import com.escaes.jobsy.domain.repository.RolRepository;
import com.escaes.jobsy.infraestructure.persistence.entity.RolEntity;
import com.escaes.jobsy.infraestructure.jpa.SpringDataRolRepository;
import com.escaes.jobsy.infraestructure.mapper.RolMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public class JpaRolRepositoryAdapter implements RolRepository {

    private final SpringDataRolRepository springDataRolRepository;
    public JpaRolRepositoryAdapter(SpringDataRolRepository springDataRolRepository) {
        this.springDataRolRepository = springDataRolRepository;
    }

    @Override
    public Optional<Rol> findById(UUID id) {
        return springDataRolRepository.findById(id).map(RolMapper::toDomain);
    }

    @Override
    public Optional<Rol> findByNombre(String nombre) {
        return springDataRolRepository.findByNombre(nombre).map(RolMapper::toDomain);
    }

    @Override
    public List<Rol> findAll() {
        return List.of();
    }

    @Override
    public void save(Rol rol) {
        RolEntity entity= RolMapper.toEntity(rol);
        springDataRolRepository.save(entity);
    }

    @Override
    public void deleteById(UUID id) {

    }

    @Override
    public void deleteByNombre(String nombre) {

    }

    @Override
    public void delete(Rol rol) {

    }

    @Override
    public void saveAndFlush(Rol rol) {
        RolEntity entity= RolMapper.toEntity(rol);
        springDataRolRepository.saveAndFlush(entity);
    }
}
