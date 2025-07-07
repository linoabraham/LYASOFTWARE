package com.tienda.virtual.service;

import com.tienda.virtual.dto.request.*;
import com.tienda.virtual.enums.EstadoOrden;
import com.tienda.virtual.enums.TipoTransaccion;
import com.tienda.virtual.exception.ResourceNotFoundException;
import com.tienda.virtual.exception.ValidationException;
import com.tienda.virtual.model.Entrega;
import com.tienda.virtual.model.Orden;
import com.tienda.virtual.model.Servicio;
import com.tienda.virtual.model.Transaccion;
import com.tienda.virtual.model.Usuario;
import com.tienda.virtual.repository.EntregaRepository;
import com.tienda.virtual.repository.OrdenRepository;
import com.tienda.virtual.repository.ServicioRepository;
import com.tienda.virtual.repository.TransaccionRepository;
import com.tienda.virtual.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrdenService {

    private final OrdenRepository ordenRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioRepository servicioRepository;
    private final TransaccionRepository transaccionRepository;
    private final EntregaRepository entregaRepository;

    public OrdenService(OrdenRepository ordenRepository, UsuarioRepository usuarioRepository, ServicioRepository servicioRepository, TransaccionRepository transaccionRepository, EntregaRepository entregaRepository) {
        this.ordenRepository = ordenRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioRepository = servicioRepository;
        this.transaccionRepository = transaccionRepository;
        this.entregaRepository = entregaRepository;
    }

    @Transactional
    public OrdenResponse crearOrden(UUID usuarioId, CrearOrdenRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Servicio servicio = servicioRepository.findById(request.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + request.getServicioId()));

        if (!servicio.isActivo()) {
            throw new ValidationException("El servicio no está activo.");
        }

        if (usuario.getMonedas() < servicio.getPrecioMonedas()) {
            throw new ValidationException("Monedas insuficientes para realizar esta orden.");
        }

        // Descontar monedas
        usuario.setMonedas(usuario.getMonedas() - servicio.getPrecioMonedas());
        usuarioRepository.save(usuario);

        // Crear la orden
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setServicio(servicio);
        orden.setEstado(EstadoOrden.PENDIENTE);
        orden.setFechaCreacion(LocalDateTime.now());
        Orden savedOrden = ordenRepository.save(orden);

        // Registrar la transacción de gasto
        Transaccion transaccion = new Transaccion();
        transaccion.setUsuario(usuario);
        transaccion.setTipo(TipoTransaccion.GASTO);
        transaccion.setCantidad(servicio.getPrecioMonedas());
        transaccion.setDescripcion("Gasto por servicio: " + servicio.getNombre());
        transaccion.setFecha(LocalDateTime.now());
        transaccionRepository.save(transaccion);

        return mapToOrdenResponse(savedOrden);
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> getOrdenesByUsuario(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        return ordenRepository.findByUsuario(usuario).stream()
                .map(this::mapToOrdenResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrdenResponse> getAllOrdenes() {
        return ordenRepository.findAll().stream()
                .map(this::mapToOrdenResponse)
                .collect(Collectors.toList());
    }

    @Transactional // Permite modificar el estado de la orden y gestionar el reembolso
    public OrdenResponse actualizarEstadoOrden(UUID ordenId, ActualizarEstadoOrdenRequest request) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + ordenId));

        // Guarda el estado actual de la orden antes de cambiarlo
        EstadoOrden estadoAnterior = orden.getEstado();
        EstadoOrden nuevoEstado = request.getNuevoEstado();

        // Lógica de transición de estados: no permitir volver atrás o saltos inválidos
        if (estadoAnterior == EstadoOrden.COMPLETADO || estadoAnterior == EstadoOrden.CANCELADO) {
            throw new ValidationException("No se puede cambiar el estado de una orden que ya está completada o cancelada.");
        }

        orden.setEstado(nuevoEstado);

        if (nuevoEstado == EstadoOrden.COMPLETADO) {
            orden.setFechaEntrega(LocalDateTime.now());
        } else if (nuevoEstado == EstadoOrden.CANCELADO) {
            orden.setFechaEntrega(null); // No hay fecha de entrega si se cancela

            // --- Lógica de Reembolso de Monedas ---
            // Solo reembolsar si no se ha reembolsado antes (es decir, el estado anterior no era CANCELADO)
            if (estadoAnterior != EstadoOrden.CANCELADO) {
                Usuario usuario = orden.getUsuario(); // Acceso al usuario asociado a la orden
                Servicio servicio = orden.getServicio(); // Acceso al servicio asociado a la orden

                int monedasAReembolsar = servicio.getPrecioMonedas();

                usuario.setMonedas(usuario.getMonedas() + monedasAReembolsar); // Sumar monedas al usuario
                usuarioRepository.save(usuario); // Guardar el usuario con las monedas actualizadas

                // Registrar la transacción de reembolso
                Transaccion transaccion = new Transaccion();
                transaccion.setUsuario(usuario);
                transaccion.setTipo(TipoTransaccion.REEMBOLSO); // Asumiendo que tienes un TipoTransaccion.REEMBOLSO
                transaccion.setCantidad(monedasAReembolsar);
                transaccion.setDescripcion("Reembolso por cancelación de la orden: " + orden.getId());
                transaccion.setFecha(LocalDateTime.now());
                transaccionRepository.save(transaccion);
            }
        } else {
            orden.setFechaEntrega(null); // Para otros estados que no son completado o cancelado
        }

        Orden updatedOrden = ordenRepository.save(orden);
        return mapToOrdenResponse(updatedOrden);
    }


    @Transactional
    public OrdenResponse agregarDatosEntrega(UUID ordenId, EntregaRequest request) {
        Orden orden = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("Orden no encontrada con ID: " + ordenId));

        if (!orden.getServicio().isRequiereEntrega()) {
            throw new ValidationException("El servicio asociado a esta orden no requiere datos de entrega.");
        }

        if (orden.getEntrega() != null) {
            Entrega existingEntrega = orden.getEntrega();
            existingEntrega.setUsuarioCuenta(request.getUsuarioCuenta());
            existingEntrega.setClave(request.getClave());
            existingEntrega.setNota(request.getNota());
            entregaRepository.save(existingEntrega);
        } else {
            Entrega entrega = new Entrega();
            entrega.setOrden(orden);
            entrega.setUsuarioCuenta(request.getUsuarioCuenta());
            entrega.setClave(request.getClave());
            entrega.setNota(request.getNota());
            Entrega savedEntrega = entregaRepository.save(entrega);
            orden.setEntrega(savedEntrega);
            ordenRepository.save(orden);
        }

        return mapToOrdenResponse(orden);
    }

    private OrdenResponse mapToOrdenResponse(Orden orden) {
        OrdenResponse response = new OrdenResponse();
        response.setId(orden.getId());
        response.setUsuarioId(orden.getUsuario().getId());
        response.setUsuarioNombre(orden.getUsuario().getNombre());
        response.setServicioId(orden.getServicio().getId());
        response.setServicioNombre(orden.getServicio().getNombre());
        response.setEstado(orden.getEstado());
        response.setFechaCreacion(orden.getFechaCreacion());
        response.setFechaEntrega(orden.getFechaEntrega());
        response.setTiempoEstimadoEspera(orden.getServicio().getTiempoEsperaMinutos() + " minutos");

        if (orden.getEntrega() != null) {
            DeliveryDetailsResponse entregaResponse = new DeliveryDetailsResponse();
            entregaResponse.setId(orden.getEntrega().getId());
            entregaResponse.setUsuarioCuenta(orden.getEntrega().getUsuarioCuenta());
            entregaResponse.setClave(orden.getEntrega().getClave());
            entregaResponse.setNota(orden.getEntrega().getNota());
            response.setEntrega(entregaResponse);
        } else {
            response.setEntrega(null);
        }
        return response;
    }
}