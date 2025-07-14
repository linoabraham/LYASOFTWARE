package com.tienda.virtual.controller;

import com.tienda.virtual.dto.request.TransaccionResponse;
import com.tienda.virtual.service.TransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/transacciones")
@Tag(name = "Transacciones", description = "Gestión de transacciones de monedas")
public class TransaccionController {

    private final TransaccionService transaccionService;

    public TransaccionController(TransaccionService transaccionService) {
        this.transaccionService = transaccionService;
    }

    @Operation(summary = "Obtener todas mis transacciones (CLIENTE)")
    @GetMapping("/mis/{usuarioId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<List<TransaccionResponse>> getMisTransacciones(@PathVariable UUID usuarioId) {
        // The service layer now ensures the DTO conversion happens correctly
        // within a transactional context, resolving the lazy loading issue.
        return ResponseEntity.ok(transaccionService.getTransaccionesByUsuario(usuarioId));
    }
}