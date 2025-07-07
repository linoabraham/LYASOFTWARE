package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.TipoTransaccion;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data // Genera getters, setters, toString, equals, hashCode automáticamente
public class TransactionResponse {
    private UUID id;
    private UUID usuarioId;
    private String usuarioNombre; // Nombre del usuario
    private TipoTransaccion tipo;
    private double cantidad;
    private String descripcion;
    private LocalDateTime fecha;
}