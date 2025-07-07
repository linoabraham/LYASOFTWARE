// src/main/java/com/tienda/virtual/dto/response/UsuarioResponse.java
package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.RolUsuario;
import lombok.Data;

import java.util.UUID;

@Data
public class UsuarioResponse {
    private UUID id;
    private String nombre;
    private String email;
    private RolUsuario rol;
    private int monedas;
}