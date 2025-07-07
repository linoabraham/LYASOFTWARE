// src/main/java/com/tienda/virtual/dto/request/EntregaRequest.java
package com.tienda.virtual.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EntregaRequest {
    @NotBlank(message = "La cuenta de usuario de entrega es obligatoria")
    private String usuarioCuenta;

    @NotBlank(message = "La clave de entrega es obligatoria")
    private String clave;

    private String nota; // Opcional
}