// src/main/java/com/tienda/virtual/dto/response/ServicioResponse.java
package com.tienda.virtual.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ServicioResponse {
    private UUID id;
    private String nombre;
    private String descripcion;
    private int precioMonedas;
    private boolean requiereEntrega;
    private boolean activo;
    private int tiempoEsperaMinutos;
    private LocalDateTime fechaCreacion;
    private String imgUrl;
    private String enlace; // Nueva variable: Enlace

    // DTO anidado para los detalles
    private DetallesServicioResponse detalles; // Nueva variable: Detalles anidados

    // Opcional: si quieres un constructor o método estático para mapear desde la entidad
    // public static ServicioResponse fromEntity(Servicio servicio) { ... }
}