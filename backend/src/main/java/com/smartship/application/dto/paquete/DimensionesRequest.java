package com.smartship.application.dto.paquete;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DimensionesRequest(
        @NotNull(message = "El largo es obligatorio")
        @Positive(message = "El largo debe ser mayor que cero")
        Double largo,

        @NotNull(message = "El ancho es obligatorio")
        @Positive(message = "El ancho debe ser mayor que cero")
        Double ancho,

        @NotNull(message = "El alto es obligatorio")
        @Positive(message = "El alto debe ser mayor que cero")
        Double alto
) {}
