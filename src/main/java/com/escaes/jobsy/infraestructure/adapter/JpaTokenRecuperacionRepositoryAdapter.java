package com.escaes.jobsy.infraestructure.adapter;

import com.escaes.jobsy.domain.model.TokenRecuperacion;
import com.escaes.jobsy.domain.repository.TokenRecuperacionRepository;
import com.escaes.jobsy.infraestructure.jpa.SpringDataTokenRecuperacionRepository;
import com.escaes.jobsy.infraestructure.mapper.TokenRecuperacionMapper;
import com.escaes.jobsy.infraestructure.persistence.entity.TokenRecuperacionEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTokenRecuperacionRepositoryAdapter implements TokenRecuperacionRepository {

    private final SpringDataTokenRecuperacionRepository springDataTokenRecuperacionRepository;

    public JpaTokenRecuperacionRepositoryAdapter(
            SpringDataTokenRecuperacionRepository springDataTokenRecuperacionRepository) {
        this.springDataTokenRecuperacionRepository = springDataTokenRecuperacionRepository;
    }

    @Override
    public void save(TokenRecuperacion token) {
        // Se conserva la fecha de creación si el token ya existía (ej. al marcarlo como usado)
        TokenRecuperacionEntity entity = springDataTokenRecuperacionRepository.findById(token.id())
                .map(existente -> {
                    existente.setUsado(token.usado());
                    existente.setExpiraEn(token.expiraEn());
                    return existente;
                })
                .orElseGet(() -> TokenRecuperacionMapper.toEntity(token));
        springDataTokenRecuperacionRepository.save(entity);
    }

    @Override
    public Optional<TokenRecuperacion> findByTokenHash(String tokenHash) {
        return springDataTokenRecuperacionRepository.findByTokenHash(tokenHash)
                .map(TokenRecuperacionMapper::toDomain);
    }

    @Override
    public void invalidarTokensDeUsuario(Long usuarioDocumento) {
        springDataTokenRecuperacionRepository.invalidarTokensDeUsuario(usuarioDocumento);
    }
}
