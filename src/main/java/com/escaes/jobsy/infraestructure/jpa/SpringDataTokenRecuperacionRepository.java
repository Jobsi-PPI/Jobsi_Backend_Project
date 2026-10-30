package com.escaes.jobsy.infraestructure.jpa;

import com.escaes.jobsy.infraestructure.persistence.entity.TokenRecuperacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataTokenRecuperacionRepository extends JpaRepository<TokenRecuperacionEntity, UUID> {

    Optional<TokenRecuperacionEntity> findByTokenHash(String tokenHash);

    @Transactional
    @Modifying
    @Query("""
            UPDATE TokenRecuperacionEntity t
            SET t.usado = true
            WHERE t.usuarioDocumento = :documento
            AND t.usado = false
            """)
    void invalidarTokensDeUsuario(@Param("documento") Long documento);
}
