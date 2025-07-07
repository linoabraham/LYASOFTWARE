package com.tienda.virtual.service;

import com.tienda.virtual.dto.request.*;
import com.tienda.virtual.enums.RolUsuario;
import com.tienda.virtual.enums.TipoTransaccion;
import com.tienda.virtual.model.Transaccion;
import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.repository.TransaccionRepository;
import com.tienda.virtual.repository.UsuarioRepository;
import com.tienda.virtual.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.virtual.exception.ResourceNotFoundException;
import com.tienda.virtual.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;
    private final PasswordEncoder passwordEncoder; // Inyectar PasswordEncoder
    private final AuthenticationManager authenticationManager; // Inyectar AuthenticationManager
    private final JwtUtils jwtUtils; // Inyectar JwtUtils

    public UsuarioService(UsuarioRepository usuarioRepository, TransaccionRepository transaccionRepository,
                          PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.usuarioRepository = usuarioRepository;
        this.transaccionRepository = transaccionRepository;
        this.passwordEncoder = passwordEncoder; // Asignar el inyectado
        this.authenticationManager = authenticationManager; // Asignar el inyectado
        this.jwtUtils = jwtUtils; // Asignar el inyectado
    }

    // Método de registro para CLIENTES
    @Transactional
    public LoginResponse registerUsuario(UsuarioRegisterRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // Usar passwordEncoder
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setMonedas(0);

        usuarioRepository.save(usuario);

        // Autenticar al usuario inmediatamente después del registro para generar el token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        LoginResponse response = new LoginResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol());
        response.setMessage("Usuario registrado exitosamente.");
        response.setToken(jwt); // Añadir el token
        return response;
    }

    // Método para que el ADMIN cree usuarios (sin login automático)
    @Transactional
    public UsuarioResponse createUsuarioByAdmin(UsuarioCreateAdminRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // Usar passwordEncoder
        usuario.setRol(request.getRol());
        usuario.setMonedas(0);

        usuarioRepository.save(usuario);
        return mapToUsuarioResponse(usuario);
    }

    // Método de login
    public LoginResponse loginUsuario(UsuarioLoginRequest request) {
        // Autenticar con Spring Security
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + request.getEmail()));


        LoginResponse response = new LoginResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol());
        response.setMessage("Login exitoso.");
        response.setToken(jwt); // Añadir el token
        return response;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAllUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponse recargarMonedas(UUID usuarioId, RecargarMonedasRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        usuario.setMonedas(usuario.getMonedas() + request.getCantidad());
        usuarioRepository.save(usuario);

        Transaccion transaccion = new Transaccion();
        transaccion.setUsuario(usuario);
        transaccion.setTipo(TipoTransaccion.RECARGA);
        transaccion.setCantidad(request.getCantidad());
        transaccion.setDescripcion("Recarga de monedas por ADMIN");
        transaccion.setFecha(LocalDateTime.now());
        transaccionRepository.save(transaccion);

        return mapToUsuarioResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> getClientes() {
        return usuarioRepository.findByRol(RolUsuario.CLIENTE).stream()
                .map(this::mapToUsuarioResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioResponse getUsuarioById(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        return mapToUsuarioResponse(usuario);
    }

    @Transactional
    public UsuarioResponse resetPasswordByAdmin(UUID usuarioId, PasswordResetAdminRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        usuario.setPassword(passwordEncoder.encode(request.getNewPassword()));
        Usuario updatedUsuario = usuarioRepository.save(usuario);
        return mapToUsuarioResponse(updatedUsuario);
    }

    private UsuarioResponse mapToUsuarioResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol());
        response.setMonedas(usuario.getMonedas());
        return response;
    }
}