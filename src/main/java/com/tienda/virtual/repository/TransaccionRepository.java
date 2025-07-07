package com.tienda.virtual.repository;

import com.tienda.virtual.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, UUID> {
    List<Transaccion> findByUsuarioId(UUID usuarioId);
}