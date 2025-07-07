package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.EstadoOrden;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data // Genera getters, setters, toString, equals, hashCode automáticamente
public class OrdenResponse {
    private UUID id;
    private UUID usuarioId;
    private String usuarioNombre; // Para el nombre del usuario
    private UUID servicioId;
    private String servicioNombre; // Para el nombre del servicio
    private EstadoOrden estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrega; // Puede ser null
    private String tiempoEstimadoEspera; // "X minutos"
    private DeliveryDetailsResponse entrega; // DTO para los detalles de entrega
}