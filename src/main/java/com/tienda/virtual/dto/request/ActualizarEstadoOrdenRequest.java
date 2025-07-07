// src/main/java/com/tienda/virtual/dto/request/ActualizarEstadoOrdenRequest.java
package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.EstadoOrden;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoOrdenRequest {
    @NotNull(message = "El estado no puede ser nulo")
    private EstadoOrden nuevoEstado;
}