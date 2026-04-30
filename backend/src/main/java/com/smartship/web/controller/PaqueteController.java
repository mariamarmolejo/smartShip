package com.smartship.web.controller;

import com.smartship.application.dto.paquete.CrearPaqueteRequest;
import com.smartship.application.dto.paquete.PaqueteResponse;
import com.smartship.application.service.PaqueteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
@RequiredArgsConstructor
@Tag(name = "Paquetes", description = "Gestión de paquetes y envíos")
public class PaqueteController {

    private final PaqueteService paqueteService;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Crear paquete", description = "Solo el ADMINISTRADOR puede crear paquetes. Estado inicial: RECIBIDO")
    public ResponseEntity<PaqueteResponse> crearPaquete(@Valid @RequestBody CrearPaqueteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paqueteService.crearPaquete(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'REPARTIDOR')")
    @Operation(summary = "Listar paquetes", description = "Devuelve todos los paquetes registrados")
    public ResponseEntity<List<PaqueteResponse>> listarPaquetes() {
        return ResponseEntity.ok(paqueteService.listarPaquetes());
    }
}
