package com.tienda.virtual.repository;

import com.tienda.virtual.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, UUID> {
    List<Servicio> findByActivoTrue();
}