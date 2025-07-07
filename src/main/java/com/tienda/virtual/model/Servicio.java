package com.tienda.virtual.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Servicio {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private int precioMonedas;

    @Column(nullable = false)
    private boolean requiereEntrega;

    @Column(nullable = false)
    private boolean activo;

    @Column(nullable = false)
    private int tiempoEsperaMinutos;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    @Column(nullable = true, length = 1024) // Campo para la URL de la imagen. Puede ser nulo.
    private String imgUrl; // ¡Nueva variable para la URL de la imagen!
}