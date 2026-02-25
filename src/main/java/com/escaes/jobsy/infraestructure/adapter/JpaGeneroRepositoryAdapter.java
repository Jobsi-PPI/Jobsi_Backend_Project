package com.escaes.jobsy.infraestructure.adapter;

import com.escaes.jobsy.domain.model.Genero;
import com.escaes.jobsy.domain.repository.GeneroRepository;
import com.escaes.jobsy.infraestructure.persistence.entity.GeneroEntity;
import com.escaes.jobsy.infraestructure.jpa.SpringDataGeneroRepository;
import com.escaes.jobsy.infraestructure.mapper.GeneroMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaGeneroRepositoryAdapter implements GeneroRepository {

    private final SpringDataGeneroRepository springDataGeneroRepository;

    public JpaGeneroRepositoryAdapter(SpringDataGeneroRepository springDataGeneroRepository) {
        this.springDataGeneroRepository = springDataGeneroRepository;
    }


    @Override
    public void save(Genero genero) {

        GeneroEntity generoEntity= new GeneroEntity();
        generoEntity.setNombreGenero(genero.nombreGenero());
        springDataGeneroRepository.save(generoEntity);

    }

    @Override
    public Optional<Genero> findById(Long id) {
        Optional<GeneroEntity> generoEntityOptional = springDataGeneroRepository.findById(id);
        if (generoEntityOptional.isPresent()) {
            GeneroEntity generoEntity = generoEntityOptional.get();
            return Optional.of(GeneroMapper.toDomain(generoEntity));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Genero> findByNombre(String nombre) {
        Optional<GeneroEntity>generoEntityOptional= springDataGeneroRepository.findByNombreGenero(nombre);
        if(generoEntityOptional.isPresent()){
            GeneroEntity generoEntity = generoEntityOptional.get();
            return Optional.of(GeneroMapper.toDomain(generoEntity));
        }
        return Optional.empty();
    }

    @Override
    public List<Genero> findAll() {
        return springDataGeneroRepository.findAll().stream()
                .map(GeneroMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public void deleteByNombre(String nombre) {

    }

    @Override
    public void delete(String genero) {

    }
}
