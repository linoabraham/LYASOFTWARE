package com.tienda.virtual.controller;

import com.tienda.virtual.dto.request.ActualizarEstadoOrdenRequest;
import com.tienda.virtual.dto.request.CrearOrdenRequest;
import com.tienda.virtual.dto.request.EntregaRequest;
import com.tienda.virtual.dto.request.OrdenResponse;
import com.tienda.virtual.service.OrdenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ordenes")
@Tag(name = "Órdenes", description = "Gestión de órdenes de servicios")
public class OrdenController {

    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @Operation(summary = "Crear una nueva orden (CLIENTE)")
    @PostMapping("/{usuarioId}")
    public ResponseEntity<OrdenResponse> crearOrden(@PathVariable UUID usuarioId, @Valid @RequestBody CrearOrdenRequest request) {
        return new ResponseEntity<>(ordenService.crearOrden(usuarioId, request), HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todas las órdenes de un cliente (CLIENTE)")
    @GetMapping("/mis/{usuarioId}")
    public ResponseEntity<List<OrdenResponse>> getMisOrdenes(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(ordenService.getOrdenesByUsuario(usuarioId));
    }

    @Operation(summary = "Obtener todas las órdenes (ADMIN)")
    @GetMapping
    public ResponseEntity<List<OrdenResponse>> getAllOrdenes() {
        return ResponseEntity.ok(ordenService.getAllOrdenes());
    }

    @Operation(summary = "Actualizar el estado de una orden (ADMIN)")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenResponse> actualizarEstadoOrden(@PathVariable UUID id, @Valid @RequestBody ActualizarEstadoOrdenRequest request) {
        return ResponseEntity.ok(ordenService.actualizarEstadoOrden(id, request));
    }

    @Operation(summary = "Agregar datos de entrega a una orden (ADMIN)")
    @PatchMapping("/{id}/entrega")
    public ResponseEntity<OrdenResponse> agregarDatosEntrega(@PathVariable UUID id, @Valid @RequestBody EntregaRequest request) {
        return ResponseEntity.ok(ordenService.agregarDatosEntrega(id, request));
    }
}