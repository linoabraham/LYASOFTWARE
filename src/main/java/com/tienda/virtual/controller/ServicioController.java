package com.tienda.virtual.controller;

import com.tienda.virtual.dto.request.ServicioRequest;
import com.tienda.virtual.dto.request.ServicioResponse;
import com.tienda.virtual.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/servicios")
@Tag(name = "Servicios", description = "Gestión de servicios disponibles")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @Operation(summary = "Obtener todos los servicios (Clientes y Admin)")
    @GetMapping
    public ResponseEntity<List<ServicioResponse>> getAllServicios() {
        return ResponseEntity.ok(servicioService.getAllServicios());
    }

    @Operation(summary = "Crear un nuevo servicio (ADMIN)")
    @PostMapping
    public ResponseEntity<ServicioResponse> createServicio(@Valid @RequestBody ServicioRequest request) {
        return new ResponseEntity<>(servicioService.createServicio(request), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un servicio existente (ADMIN)")
    @PatchMapping("/{id}")
    public ResponseEntity<ServicioResponse> updateServicio(@PathVariable UUID id, @Valid @RequestBody ServicioRequest request) {
        return ResponseEntity.ok(servicioService.updateServicio(id, request));
    }

    @Operation(summary = "Eliminar un servicio (ADMIN)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServicio(@PathVariable UUID id) {
        servicioService.deleteServicio(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener un servicio por su ID")
    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponse> getServicioById(@PathVariable UUID id) {
        return ResponseEntity.ok(servicioService.getServicioById(id));
    }

    @Operation(summary = "Obtener todos los servicios activos (Clientes y Admin)")
    @GetMapping("/activos") // <-- New endpoint for active services
    public ResponseEntity<List<ServicioResponse>> getActiveServicios() {
        return ResponseEntity.ok(servicioService.getActiveServicios());
    }
}