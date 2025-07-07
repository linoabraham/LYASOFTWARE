package com.tienda.virtual.repository;

import com.tienda.virtual.model.Orden;
import com.tienda.virtual.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, UUID> {
    List<Orden> findByUsuarioId(UUID usuarioId);
    List<Orden> findByUsuario(Usuario usuario);

}