package com.smartship.application.dto.paquete;

import java.time.LocalDateTime;

public record PaqueteResponse(
        Long id,
        String trackingId,
        Double peso,
        DimensionesResponse dimensiones,
        String destinatario,
        String estado,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {}
