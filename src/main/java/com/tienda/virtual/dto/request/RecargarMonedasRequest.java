// src/main/java/com/tienda/virtual/dto/request/RecargarMonedasRequest.java
package com.tienda.virtual.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecargarMonedasRequest {
    @NotNull(message = "La cantidad no puede ser nula")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
}
