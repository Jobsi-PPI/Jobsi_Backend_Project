package com.escaes.jobsy.infraestructure.adapter;

import com.escaes.jobsy.domain.model.Trabajo;
import com.escaes.jobsy.domain.repository.TrabajoRepository;
import com.escaes.jobsy.infraestructure.jpa.SpringDataTrabajoRepository;
import com.escaes.jobsy.infraestructure.mapper.TrabajoMapper;
import com.escaes.jobsy.infraestructure.persistence.criteria.TrabajoCriteriaRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaTrabajoRepositoryAdapter implements TrabajoRepository {

    private final SpringDataTrabajoRepository springDataTrabajoRepository;

    private final TrabajoCriteriaRepository trabajoCriteriaRepository;

    public JpaTrabajoRepositoryAdapter(SpringDataTrabajoRepository springDataTrabajoRepository,
            TrabajoCriteriaRepository trabajoCriteriaRepository) {
        this.springDataTrabajoRepository = springDataTrabajoRepository;
        this.trabajoCriteriaRepository = trabajoCriteriaRepository;
    }

    @Override
    public Optional<Trabajo> findById(UUID id) {

        return springDataTrabajoRepository.findById(id).map(TrabajoMapper::toDomain);
    }

    @Override
    public Optional<Trabajo> findByNombre(String nombre) {
        return springDataTrabajoRepository.findByDescripcion(nombre).map(TrabajoMapper::toDomain);
    }

    @Override
    public void save(Trabajo trabajo) {
        springDataTrabajoRepository.save(TrabajoMapper.toEntity(trabajo));
    }

    @Override
    public void deleteById(UUID id) {
        springDataTrabajoRepository.deleteById(id);
    }

    @Override
    public void delete(Trabajo trabajo) {
        springDataTrabajoRepository.delete(TrabajoMapper.toEntity(trabajo));
    }

    @Override
    public List<Trabajo> findAll() {
        return springDataTrabajoRepository.findAll().stream().map(TrabajoMapper::toDomain).toList();
    }

    @Override
    public List<Trabajo> findBySolicitanteCorreo(String solicitanteCorreo) {
        return springDataTrabajoRepository.findBySolicitanteCorreo(solicitanteCorreo).stream().map(TrabajoMapper::toDomain).toList();
    }

    @Override
    public List<Trabajo> findByTrabajadorCorreo(String TrabajadorCorreo) {
        return springDataTrabajoRepository.findByTrabajadorCorreo(TrabajadorCorreo).stream().map(TrabajoMapper::toDomain).toList();
    }

    /*
        Metodos para encontrar trabajos por estado y categoria (Pendientes)
     */
    @Override
    public List<Trabajo> findByEstado(String estado) {
        return List.of();
    }

    @Override
    public List<Trabajo> findByCategoria(String categoria) {
        return List.of();
    }

    @Override
    public List<Trabajo> findByCategoriaAndEstado(String categoria, String estado) {
        return List.of();
    }

    /*
    Especificos a un usuario (Pendientes)
    */

    @Override
    public List<Trabajo> findBySolicitanteCorreoAndEstado(String correo, String estado) {
        return List.of();
    }

    @Override
    public List<Trabajo> findBySolicitanteCorreoAndCategoria(String correo, String categoria) {
        return List.of();
    }

    @Override
    public List<Trabajo> findBySolicitanteCorreoAndCategoriaAndEstado(String correo, String categoria, String estado) {
        return List.of();
    }

    @Override
    public List<Trabajo> findByTrabajadorCorreoAndEstado(String correo, String estado) {
        return springDataTrabajoRepository.findByTrabajadorCorreoAndEstadoNombre(correo, estado).stream().map(TrabajoMapper::toDomain).toList();
    }

    @Override
    public List<Trabajo> findByTrabajadorCorreoAndCategoria(String correo, String categoria) {
        return List.of();
    }

    @Override
    public List<Trabajo> findByTrabajadorCorreoAndCategoriaAndEstado(String correo, String categoria, String estado) {
        return List.of();
    }

    @Override
    public long count() {
        return springDataTrabajoRepository.count();
    }

    @Override
    public Map<String, Long> contarPorEstado() {
        return aMapa(springDataTrabajoRepository.contarPorEstado());
    }

    @Override
    public Map<String, Long> contarPorCategoria() {
        return aMapa(springDataTrabajoRepository.contarPorCategoria());
    }

    @Override
    public long contarPorUsuario(Long documento) {
        return springDataTrabajoRepository.contarPorUsuario(documento);
    }

    private static Map<String, Long> aMapa(List<Object[]> filas) {
        Map<String, Long> resultado = new LinkedHashMap<>();
        for (Object[] fila : filas) {
            resultado.put((String) fila[0], (Long) fila[1]);
        }
        return resultado;
    }

    @Override
    public List<Trabajo> buscarTrabajosCriteria(String titulo, String categoria, String estado,
            String ubicacion, String tipoPago, Double pagoMin, Double pagoMax,
            String solicitanteCorreo, int size, int page) {
        return trabajoCriteriaRepository
                .buscar(titulo, categoria, estado, ubicacion, tipoPago, pagoMin, pagoMax,
                        solicitanteCorreo, size, page)
                .stream()
                .map(TrabajoMapper::toDomain)
                .toList();
    }

}
