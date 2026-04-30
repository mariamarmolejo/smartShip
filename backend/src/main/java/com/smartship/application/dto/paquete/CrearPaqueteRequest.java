package com.smartship.application.dto.paquete;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CrearPaqueteRequest(
        @NotNull(message = "El peso es obligatorio")
        @Positive(message = "El peso debe ser mayor que cero")
        Double peso,

        @NotNull(message = "Las dimensiones son obligatorias")
        @Valid
        DimensionesRequest dimensiones,

        @NotBlank(message = "El destinatario es obligatorio")
        String destinatario
) {}
