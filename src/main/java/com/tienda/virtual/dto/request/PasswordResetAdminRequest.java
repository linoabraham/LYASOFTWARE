package com.tienda.virtual.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetAdminRequest {
    @NotBlank(message = "El nuevo password es obligatorio")
    @Size(min = 6, message = "El password debe tener al menos 6 caracteres")
    private String newPassword;
}