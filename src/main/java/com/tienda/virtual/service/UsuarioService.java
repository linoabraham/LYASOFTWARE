package com.tienda.virtual.service;

import com.tienda.virtual.dto.request.*;
import com.tienda.virtual.enums.RolUsuario;
import com.tienda.virtual.enums.TipoTransaccion;
import com.tienda.virtual.model.CorreoVerificacion;
import com.tienda.virtual.model.Transaccion;
import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.repository.CorreoVerificacionRepository;
import com.tienda.virtual.repository.TransaccionRepository;
import com.tienda.virtual.repository.UsuarioRepository;
import com.tienda.virtual.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tienda.virtual.exception.ResourceNotFoundException;
import com.tienda.virtual.exception.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;
    private final CorreoVerificacionRepository correoVerificacionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepository usuarioRepository, TransaccionRepository transaccionRepository,
                          CorreoVerificacionRepository correoVerificacionRepository,
                          PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager,
                          JwtUtils jwtUtils, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.transaccionRepository = transaccionRepository;
        this.correoVerificacionRepository = correoVerificacionRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    @Transactional
    public MensajeResponse solicitarRegistro(SolicitarRegistroRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("El email ya está registrado en una cuenta activa.");
        }

        Optional<CorreoVerificacion> existingVerification = correoVerificacionRepository.findByEmail(request.getEmail());
        if (existingVerification.isPresent() && !existingVerification.get().isVerificado() && existingVerification.get().getFechaExpiracion().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Ya existe una solicitud de verificación pendiente para este email. Por favor, revisa tu bandeja de entrada o espera a que expire para solicitar una nueva.");
        }

        String codigoVerificacion = String.format("%06d", new Random().nextInt(999999));
        String passwordEncriptada = passwordEncoder.encode(request.getPassword());

        CorreoVerificacion correoVerificacion = existingVerification.orElse(new CorreoVerificacion());
        correoVerificacion.setNombre(request.getNombre());
        correoVerificacion.setEmail(request.getEmail());
        correoVerificacion.setPasswordEncriptada(passwordEncriptada);
        correoVerificacion.setCodigoVerificacion(codigoVerificacion);
        correoVerificacion.setFechaExpiracion(LocalDateTime.now().plusMinutes(10));
        correoVerificacion.setVerificado(false);
        correoVerificacionRepository.save(correoVerificacion);

        emailService.sendVerificationEmail(request.getEmail(), request.getNombre(), codigoVerificacion);

        return new MensajeResponse("Código de verificación enviado a " + request.getEmail());
    }

    @Transactional
    public LoginResponse verificarCodigo(VerificarCodigoRequest request) {
        CorreoVerificacion correoVerificacion = correoVerificacionRepository
                .findByEmailAndCodigoVerificacion(request.getEmail(), request.getCodigo())
                .orElseThrow(() -> new ValidationException("Código de verificación o email inválido."));

        if (correoVerificacion.isVerificado()) {
            throw new ValidationException("Este código ya ha sido utilizado para verificar una cuenta.");
        }

        if (correoVerificacion.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            correoVerificacionRepository.delete(correoVerificacion);
            throw new ValidationException("El código de verificación ha expirado. Por favor, solicita uno nuevo.");
        }

        // Si el código es correcto y no ha expirado, crear el usuario real
        Usuario usuario = new Usuario();
        usuario.setNombre(correoVerificacion.getNombre());
        usuario.setEmail(correoVerificacion.getEmail());
        usuario.setPassword(correoVerificacion.getPasswordEncriptada());
        usuario.setRol(RolUsuario.CLIENTE);
        usuario.setMonedas(0);

        usuarioRepository.save(usuario);

        // Marcar como verificado y eliminar la entrada temporal
        correoVerificacion.setVerificado(true);
        correoVerificacionRepository.delete(correoVerificacion); // Eliminar después de crear el usuario

        // Generar el token JWT directamente para el usuario recién creado.
        // NO necesitamos autenticar con AuthenticationManager aquí, ya que la verificación del código
        // es la que valida la creación del usuario.
        String jwt = jwtUtils.generateTokenForUser(usuario.getEmail());

        LoginResponse response = new LoginResponse();
        response.setId(usuario.getId());
        response.setNombre(usuario.getNombre());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol());
        response.setMessage("Cuenta verificada y usuario registrado exitosamente.");
        response.setToken(jwt);
        return response;
    }

    // Método de login (sin cambios)
    public LoginResponse loginUsuario(UsuarioLoginRequest request) {
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
        response.setToken(jwt);
        return response;
    }

    // El resto de métodos de UsuarioService se mantienen sin cambios.
    @Transactional
    public UsuarioResponse createUsuarioByAdmin(UsuarioCreateAdminRequest request) {
        if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("El email ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setMonedas(0);

        usuarioRepository.save(usuario);
        return mapToUsuarioResponse(usuario);
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
