package com.tienda.virtual.service;

import com.tienda.virtual.dto.request.ServicioRequest;
import com.tienda.virtual.dto.request.ServicioResponse;
import com.tienda.virtual.exception.ResourceNotFoundException;
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

    @Transactional(readOnly = true) // Best practice for read-only operations
    public List<ServicioResponse> getAllServicios() {
        return servicioRepository.findAll().stream()
                .map(this::mapToServicioResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true) // Best practice for read-only operations
    public List<ServicioResponse> getActiveServicios() {
        // Assuming you have this method in your ServicioRepository:
        // List<Servicio> findByActivoTrue();
        return servicioRepository.findByActivoTrue().stream()
                .map(this::mapToServicioResponse)
                .collect(Collectors.toList());
    }

    @Transactional // Ensures the save operation is atomic
    public ServicioResponse createServicio(ServicioRequest request) {
        Servicio servicio = new Servicio();
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecioMonedas(request.getPrecioMonedas());
        servicio.setRequiereEntrega(request.getRequiereEntrega());
        servicio.setActivo(request.getActivo());
        servicio.setTiempoEsperaMinutos(request.getTiempoEsperaMinutos());
        servicio.setFechaCreacion(LocalDateTime.now());
        servicio.setImgUrl(request.getImgUrl()); // <-- NEW: Set the image URL
        Servicio savedServicio = servicioRepository.save(servicio);
        return mapToServicioResponse(savedServicio);
    }

    @Transactional // Ensures the update operation is atomic
    public ServicioResponse updateServicio(UUID id, ServicioRequest request) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con ID: " + id));

        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecioMonedas(request.getPrecioMonedas());
        servicio.setRequiereEntrega(request.getRequiereEntrega());
        servicio.setActivo(request.getActivo());
        servicio.setTiempoEsperaMinutos(request.getTiempoEsperaMinutos());
        servicio.setImgUrl(request.getImgUrl()); // <-- NEW: Update the image URL
        Servicio updatedServicio = servicioRepository.save(servicio);
        return mapToServicioResponse(updatedServicio);
    }

    @Transactional // Ensures the delete operation is atomic
    public void deleteServicio(UUID id) {
        if (!servicioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio no encontrado con ID: " + id);
        }
        servicioRepository.deleteById(id);
    }

    @Transactional(readOnly = true) // Best practice for read-only operations
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
        response.setImgUrl(servicio.getImgUrl()); // <-- NEW: Map the image URL
        return response;
    }
}