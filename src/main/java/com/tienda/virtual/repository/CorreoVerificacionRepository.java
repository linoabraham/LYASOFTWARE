// src/main/java/com/tienda/virtual/repository/CorreoVerificacionRepository.java
package com.tienda.virtual.repository;

import com.tienda.virtual.model.CorreoVerificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CorreoVerificacionRepository extends JpaRepository<CorreoVerificacion, UUID> {
    Optional<CorreoVerificacion> findByEmailAndCodigoVerificacion(String email, String codigoVerificacion);
    Optional<CorreoVerificacion> findByEmail(String email);
}