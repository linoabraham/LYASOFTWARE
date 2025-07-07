// src/main/java/com/tienda/virtual/controller/UsuarioController.java
package com.tienda.virtual.controller;

import com.tienda.virtual.dto.request.*;
import com.tienda.virtual.service.OrdenService;
import com.tienda.virtual.service.TransaccionService;
import com.tienda.virtual.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Gestión de usuarios y sus operaciones")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final OrdenService ordenService;
    private final TransaccionService transaccionService;

    public UsuarioController(UsuarioService usuarioService, OrdenService ordenService, TransaccionService transaccionService) {
        this.usuarioService = usuarioService;
        this.ordenService = ordenService;
        this.transaccionService = transaccionService;
    }

    @Operation(summary = "Crear un nuevo usuario (ADMIN) - Permite especificar el rol")
    @PostMapping
    public ResponseEntity<UsuarioResponse> createUsuarioByAdmin(@Valid @RequestBody UsuarioCreateAdminRequest request) {
        UsuarioResponse response = usuarioService.createUsuarioByAdmin(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todos los usuarios (ADMIN)")
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.getAllUsuarios());
    }

    @Operation(summary = "Obtener solo usuarios con rol CLIENTE (ADMIN)")
    @GetMapping("/clientes") // Nuevo endpoint específico para clientes
    public ResponseEntity<List<UsuarioResponse>> getClientes() {
        return ResponseEntity.ok(usuarioService.getClientes());
    }

    @Operation(summary = "Obtener un usuario por su ID (ADMIN o el propio USUARIO)")
    @GetMapping("/{id}") // Endpoint para obtener un usuario por ID
    public ResponseEntity<UsuarioResponse> getUsuarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @Operation(summary = "Recargar monedas a un usuario (ADMIN)")
    @PatchMapping("/{id}/recargar")
    public ResponseEntity<UsuarioResponse> recargarMonedas(@PathVariable UUID id, @Valid @RequestBody RecargarMonedasRequest request) {
        return ResponseEntity.ok(usuarioService.recargarMonedas(id, request));
    }

    @Operation(summary = "Obtener órdenes de un usuario por ID (Cliente o Admin)")
    @GetMapping("/{id}/ordenes")
    public ResponseEntity<List<OrdenResponse>> getOrdenesByUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(ordenService.getOrdenesByUsuario(id));
    }

    @Operation(summary = "Obtener transacciones de un usuario por ID (Cliente o Admin)")
    @GetMapping("/{id}/transacciones")
    public ResponseEntity<List<TransaccionResponse>> getTransaccionesByUsuario(@PathVariable UUID id) {
        return ResponseEntity.ok(transaccionService.getTransaccionesByUsuario(id));
    }

    // --- NUEVO ENDPOINT: Administrar Recuperación de Contraseña por ADMIN ---
    @Operation(summary = "Resetear la contraseña de un usuario (ADMIN)",
            description = "Permite a un administrador establecer una nueva contraseña para un usuario dado su ID.")
    @PatchMapping("/{id}/reset-password") // Usamos PATCH porque es una actualización parcial de un recurso existente
    public ResponseEntity<UsuarioResponse> resetPasswordByAdmin(@PathVariable UUID id, @Valid @RequestBody PasswordResetAdminRequest request) {
        UsuarioResponse response = usuarioService.resetPasswordByAdmin(id, request);
        return ResponseEntity.ok(response);
    }
}