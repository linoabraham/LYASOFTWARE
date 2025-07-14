package com.tienda.virtual.controller;

import com.tienda.virtual.dto.request.*;
import com.tienda.virtual.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "API para registro, verificación y login de usuarios con JWT")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Paso 1: Solicitar registro y enviar código de verificación por email")
    @PostMapping("/solicitar-registro")
    public ResponseEntity<MensajeResponse> solicitarRegistro(@Valid @RequestBody SolicitarRegistroRequest request) {
        MensajeResponse response = usuarioService.solicitarRegistro(request);
        return new ResponseEntity<>(response, HttpStatus.OK); // O HttpStatus.ACCEPTED
    }

    @Operation(summary = "Paso 2: Verificar código y crear usuario, luego obtener JWT")
    @PostMapping("/verificar-codigo")
    public ResponseEntity<LoginResponse> verificarCodigo(@Valid @RequestBody VerificarCodigoRequest request) {
        LoginResponse response = usuarioService.verificarCodigo(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Iniciar sesión de usuario y obtener JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UsuarioLoginRequest request) {
        LoginResponse response = usuarioService.loginUsuario(request);
        return ResponseEntity.ok(response);
    }
}