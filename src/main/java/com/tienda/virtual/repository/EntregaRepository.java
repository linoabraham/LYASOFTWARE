package com.tienda.virtual.repository;

import com.tienda.virtual.model.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EntregaRepository extends JpaRepository<Entrega, UUID> {
}