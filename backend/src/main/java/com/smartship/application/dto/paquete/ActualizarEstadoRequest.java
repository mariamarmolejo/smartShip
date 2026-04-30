package com.smartship.application.dto.paquete;

import com.smartship.domain.model.EstadoPaquete;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        EstadoPaquete nuevoEstado
) {}
