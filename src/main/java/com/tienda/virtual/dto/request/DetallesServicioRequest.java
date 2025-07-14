package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.FrameworkProgramacion;
import com.tienda.virtual.enums.LenguajeProgramacion;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DetallesServicioRequest {
    @Size(max = 100, message = "El rubro no puede exceder 100 caracteres")
    private String rubro;

    @Size(max = 100, message = "El tipo de software no puede exceder 100 caracteres")
    private String tipoDeSoftware;

    // Aquí no usamos @NotNull en los enums si son opcionales, solo si siempre deben venir
    private LenguajeProgramacion lenguaje;
    private FrameworkProgramacion framework;
}