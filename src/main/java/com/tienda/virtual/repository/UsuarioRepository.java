package com.tienda.virtual.repository;

import com.tienda.virtual.enums.RolUsuario;
import com.tienda.virtual.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByRol(RolUsuario rol); // Nuevo método para buscar por rol
}
