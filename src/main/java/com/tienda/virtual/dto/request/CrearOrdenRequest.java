// src/main/java/com/tienda/virtual/dto/request/CrearOrdenRequest.java
package com.tienda.virtual.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CrearOrdenRequest {
    @NotNull(message = "El ID del servicio no puede ser nulo")
    private UUID servicioId;
}