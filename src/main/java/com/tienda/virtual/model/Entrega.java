package com.tienda.virtual.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Entrega {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", unique = true, nullable = false)
    private Orden orden;

    @Column(nullable = false)
    private String usuarioCuenta; // Renombrado para evitar conflicto con la entidad Usuario

    @Column(nullable = false)
    private String clave;

    @Column(length = 500)
    private String nota;
}
