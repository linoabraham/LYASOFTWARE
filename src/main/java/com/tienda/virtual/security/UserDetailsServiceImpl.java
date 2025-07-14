// src/main/java/com/tienda/virtual/security/UserDetailsServiceImpl.java
package com.tienda.virtual.security;

import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // O Collections.emptyList() si no usas roles detallados para el UserDetails

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Now, we use our custom builder to create CustomUserDetails
        return CustomUserDetails.build(usuario);
    }
}