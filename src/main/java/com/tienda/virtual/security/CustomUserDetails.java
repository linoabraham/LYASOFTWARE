// src/main/java/com/tienda/virtual/security/CustomUserDetails.java
package com.tienda.virtual.security;

import com.tienda.virtual.model.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class CustomUserDetails implements UserDetails {

    @Getter
    private UUID id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(UUID id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    // Static factory method to easily build CustomUserDetails from your Usuario model
    public static CustomUserDetails build(Usuario usuario) {
        // Spring Security expects roles to be prefixed with "ROLE_"
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());
        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPassword(),
                Collections.singletonList(authority)); // For simplicity, one role per user for now
    }


    // --- Standard UserDetails interface methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() { // Spring Security uses getUsername() as the primary identifier (e.g., email)
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // You can implement actual logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // You can implement actual logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // You can implement actual logic if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // You can implement actual logic if needed
    }
}