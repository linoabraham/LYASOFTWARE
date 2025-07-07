package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.EstadoOrden;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID id;
    private UUID usuarioId;
    private String usuarioNombre; // Nombre del usuario
    private UUID servicioId;
    private String servicioNombre; // Nombre del servicio
    private EstadoOrden estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaEntrega; // Puede ser null
    private String tiempoEstimadoEspera; // Ajustado a String para compatibilidad
    private DeliveryDetailsResponse entrega; // Usamos el DTO de entrega
}