package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.RolUsuario;
import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    private UUID id;
    private String nombre;
    private String email;
    private RolUsuario rol;
    private String message;
    private String token; // ¡NUEVO: El token JWT!
    private String type = "Bearer"; // Tipo de token
}