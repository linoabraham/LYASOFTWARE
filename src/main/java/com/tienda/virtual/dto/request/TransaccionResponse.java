package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.TipoTransaccion; // Assuming you have this enum
import lombok.Data; // From Lombok

import java.time.LocalDateTime;
import java.util.UUID;

@Data // Generates getters, setters, toString, equals, and hashCode
public class TransaccionResponse {
    private UUID id;
    private UUID usuarioId;
    private String usuarioNombre; // Nombre del usuario
    private TipoTransaccion tipo;
    private double cantidad;
    private String descripcion;
    private LocalDateTime fecha;
}