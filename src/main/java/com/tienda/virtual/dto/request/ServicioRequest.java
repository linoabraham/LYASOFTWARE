// src/main/java/com/tienda/virtual/dto/request/ServicioRequest.java
package com.tienda.virtual.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ServicioRequest {
    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotNull(message = "El precio en monedas no puede ser nulo")
    @Min(value = 0, message = "El precio en monedas debe ser igual o mayor a 0")
    private Integer precioMonedas;

    @NotNull(message = "Requiere entrega no puede ser nulo")
    private Boolean requiereEntrega;

    @NotNull(message = "Activo no puede ser nulo")
    private Boolean activo;

    @NotNull(message = "El tiempo de espera en minutos no puede ser nulo")
    @Min(value = 0, message = "El tiempo de espera debe ser igual o mayor a 0")
    private Integer tiempoEsperaMinutos;
    // ¡Nueva variable para la URL de la imagen!
    private String imgUrl; // No es @NotBlank si es opcional en la BD
}