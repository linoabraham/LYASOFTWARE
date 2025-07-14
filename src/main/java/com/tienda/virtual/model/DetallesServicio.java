// src/main/java/com/tienda/virtual/model/DetallesServicio.java
package com.tienda.virtual.model;

import com.tienda.virtual.enums.FrameworkProgramacion;
import com.tienda.virtual.enums.LenguajeProgramacion;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor; // Añadir para constructor con todos los argumentos
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable // Indica que esta clase puede ser embebida en otras entidades
@Data
@NoArgsConstructor
@AllArgsConstructor // Útil para constructores cuando mapeas desde DTOs o en pruebas
public class DetallesServicio {

    @Column(nullable = true) // Puede ser nulo si no aplica
    private String rubro; // Ej: "Desarrollo Web", "Diseño Gráfico", "Marketing Digital"

    @Column(nullable = true)
    private String tipoDeSoftware; // Ej: "Aplicación Móvil", "Sistema Web", "Software de Escritorio"

    @Enumerated(EnumType.STRING) // Almacena el nombre del enum como String en la BD
    @Column(nullable = true)
    private LenguajeProgramacion lenguaje;

    @Enumerated(EnumType.STRING) // Almacena el nombre del enum como String en la BD
    @Column(nullable = true)
    private FrameworkProgramacion framework;
}