package com.tienda.virtual.service;

import com.tienda.virtual.dto.request.TransaccionResponse;
import com.tienda.virtual.enums.TipoTransaccion;
import com.tienda.virtual.exception.ResourceNotFoundException;
import com.tienda.virtual.model.Transaccion;
import com.tienda.virtual.repository.TransaccionRepository;
import com.tienda.virtual.repository.UsuarioRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import com.tienda.virtual.model.Usuario;   // Your entity class
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final UsuarioRepository usuarioRepository;

    public TransaccionService(TransaccionRepository transaccionRepository, UsuarioRepository usuarioRepository) {
        this.transaccionRepository = transaccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // This method should be transactional as it modifies the database (saves a transaction)
    @Transactional
    public Transaccion registrarTransaccion(Usuario usuario, TipoTransaccion tipo, double cantidad, String descripcion) {
        Transaccion transaccion = new Transaccion();
        transaccion.setUsuario(usuario);
        transaccion.setTipo(tipo);
        transaccion.setDescripcion(descripcion);
        transaccion.setFecha(LocalDateTime.now());
        return transaccionRepository.save(transaccion);
    }

    @Transactional(readOnly = true) // This is the fix for "no Session" error
    public List<TransaccionResponse> getTransaccionesByUsuario(UUID usuarioId) {
        // Find the user first to ensure they exist, which prevents potential
        // issues if findByUsuarioId on transaccionRepository relies on a valid user.
        // It also makes the ResourceNotFoundException more explicit for the user ID.
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        return transaccionRepository.findByUsuarioId(usuarioId).stream()
                .map(this::mapToTransaccionResponse) // Mapeo a DTO ocurre dentro de la transacción activa
                .collect(Collectors.toList());
    }

    // Helper method to map entity to DTO
    private TransaccionResponse mapToTransaccionResponse(Transaccion transaccion) {
        TransaccionResponse response = new TransaccionResponse();
        response.setId(transaccion.getId());
        response.setUsuarioId(transaccion.getUsuario().getId());
        // Accessing the 'nombre' here forces the lazy-loaded User proxy to initialize
        // because the method is called within a @Transactional context.
        response.setUsuarioNombre(transaccion.getUsuario().getNombre());
        response.setTipo(transaccion.getTipo());
        response.setCantidad(transaccion.getCantidad());
        response.setDescripcion(transaccion.getDescripcion());
        response.setFecha(transaccion.getFecha());
        return response;
    }
}