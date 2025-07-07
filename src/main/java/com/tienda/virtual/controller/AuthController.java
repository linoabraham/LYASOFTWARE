package com.tienda.virtual.controller;

import com.tienda.virtual.dto.request.LoginResponse;
import com.tienda.virtual.dto.request.UsuarioLoginRequest;
import com.tienda.virtual.dto.request.UsuarioRegisterRequest;
import com.tienda.virtual.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "API para registro y login de usuarios con JWT")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Registrar un nuevo usuario (CLIENTE por defecto) y obtener JWT")
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody UsuarioRegisterRequest request) {
        LoginResponse response = usuarioService.registerUsuario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Iniciar sesión de usuario y obtener JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody UsuarioLoginRequest request) {
        LoginResponse response = usuarioService.loginUsuario(request);
        return ResponseEntity.ok(response);
    }
}