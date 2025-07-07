package com.tienda.virtual.dto.request;
import lombok.Data;
import java.util.UUID;

@Data
public class DeliveryDetailsResponse {
    private UUID id;
    private String usuarioCuenta;
    private String clave;
    private String nota;
}