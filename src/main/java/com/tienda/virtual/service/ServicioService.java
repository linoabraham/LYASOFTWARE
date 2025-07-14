package com.tienda.virtual.service;

import com.tienda.virtual.dto.request.DetallesServicioResponse;
import com.tienda.virtual.dto.request.ServicioRequest;
import com.tienda.virtual.dto.request.ServicioResponse;
import com.tienda.virtual.exception.ResourceNotFoundException;
import com.tienda.virtual.model.DetallesServicio;
import com.tienda.virtual.model.Servicio;
import com.tienda.virtual.repository.ServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> getAllServicios() {
        return servicioRepository.findAll().stream()
                .map(this::mapToServicioResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ServicioResponse> getActiveServicios() {
        return servicioRepository.findByActivoTrue().stream()
                .map(this::mapToServicioResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ServicioResponse createServicio(ServicioRequest request) {
        Servicio servicio = new Servicio();
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecioMonedas(request.getPrecioMonedas());
        servicio.setRequiereEntrega(request.getRequiereEntrega());
        servicio.setActivo(request.getActivo());
        servicio.setTiempoEsperaMinutos(request.getTiempoEsperaMinutos());
        servicio.setFechaCreacion(LocalDateTime.now());
        servicio.setImgUrl(request.getImgUrl());
        servicio.setEnlace(request.getEnlace()); // <-- NUEVO: set enlace

        // Mapear los detalles del DTO al modelo embebido
        if (request.getDetalles() != null) {
            servicio.setDetalles(new DetallesServicio(
                    request.getDetalles().getRubro(),
                    request.getDetalles().getTipoDeSoftware(),
                    request.getDetalles().getLenguaje(),
                    request.getDetalles().getFramework()
            ));
        } else {
            servicio.setDetalles(null); // Asegurarse de que sea nulo si no se proporciona
        }

        Servicio savedServicio = servicioRepository.save(servicio);
        return mapToServicioResponse(savedServicio);
    }

    @Transactional
    public ServicioResponse updateServicio(UUID id, ServicioRequest request) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));

        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecioMonedas(request.getPrecioMonedas());
        servicio.setRequiereEntrega(request.getRequiereEntrega());
        servicio.setActivo(request.getActivo());
        servicio.setTiempoEsperaMinutos(request.getTiempoEsperaMinutos());
        servicio.setImgUrl(request.getImgUrl());
        servicio.setEnlace(request.getEnlace()); // <-- NUEVO: update enlace

        // Mapear los detalles del DTO al modelo embebido
        if (request.getDetalles() != null) {
            // Reutiliza o crea una nueva instancia de DetallesServicio
            // Si el servicio ya tenía detalles, actualizda sus campos; si no, crea uno nuevo.
            if (servicio.getDetalles() == null) {
                servicio.setDetalles(new DetallesServicio());
            }
            servicio.getDetalles().setRubro(request.getDetalles().getRubro());
            servicio.getDetalles().setTipoDeSoftware(request.getDetalles().getTipoDeSoftware());
            servicio.getDetalles().setLenguaje(request.getDetalles().getLenguaje());
            servicio.getDetalles().setFramework(request.getDetalles().getFramework());
        } else {
            servicio.setDetalles(null); // Permite eliminar los detalles si se envía nulo
        }

        Servicio updatedServicio = servicioRepository.save(servicio);
        return mapToServicioResponse(updatedServicio);
    }

    @Transactional
    public void deleteServicio(UUID id) {
        if (!servicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ServicioResponse getServicioById(UUID id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));
        return mapToServicioResponse(servicio);
    }

    // Helper method to map entity to DTO
    private ServicioResponse mapToServicioResponse(Servicio servicio) {
        ServicioResponse response = new ServicioResponse();
        response.setId(servicio.getId());
        response.setNombre(servicio.getNombre());
        response.setDescripcion(servicio.getDescripcion());
        response.setPrecioMonedas(servicio.getPrecioMonedas());
        response.setRequiereEntrega(servicio.isRequiereEntrega());
        response.setActivo(servicio.isActivo());
        response.setTiempoEsperaMinutos(servicio.getTiempoEsperaMinutos());
        response.setFechaCreacion(servicio.getFechaCreacion());
        response.setImgUrl(servicio.getImgUrl());
        response.setEnlace(servicio.getEnlace()); // <-- NUEVO: map enlace

        // Mapear los detalles del modelo al DTO de respuesta
        if (servicio.getDetalles() != null) {
            DetallesServicioResponse detallesResponse = new DetallesServicioResponse();
            detallesResponse.setRubro(servicio.getDetalles().getRubro());
            detallesResponse.setTipoDeSoftware(servicio.getDetalles().getTipoDeSoftware());
            detallesResponse.setLenguaje(servicio.getDetalles().getLenguaje());
            detallesResponse.setFramework(servicio.getDetalles().getFramework());
            response.setDetalles(detallesResponse);
        } else {
            response.setDetalles(null); // Asegurarse de que sea nulo si no hay detalles
        }
        return response;
    }
}
