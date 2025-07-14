package com.tienda.virtual.dto.request;

import com.tienda.virtual.enums.FrameworkProgramacion;
import com.tienda.virtual.enums.LenguajeProgramacion;
import lombok.Data;

@Data
public class DetallesServicioResponse {
    private String rubro;
    private String tipoDeSoftware;
    private LenguajeProgramacion lenguaje;
    private FrameworkProgramacion framework;
    // Aquí no usamos @NotNull en los enumsd sdi son opcionales, solo si siempre deben venir
}