package com.smartship.application.mapper;

import com.smartship.application.dto.paquete.DimensionesRequest;
import com.smartship.application.dto.paquete.DimensionesResponse;
import com.smartship.application.dto.paquete.PaqueteResponse;
import com.smartship.domain.model.Dimensiones;
import com.smartship.domain.model.Paquete;
import org.springframework.stereotype.Component;

@Component
public class PaqueteMapper {

    public PaqueteResponse toResponse(Paquete paquete) {
        return new PaqueteResponse(
                paquete.getId(),
                paquete.getTrackingId(),
                paquete.getPeso(),
                toDimensionesResponse(paquete.getDimensiones()),
                paquete.getDestinatario(),
                paquete.getEstado().name(),
                paquete.getCreadoEn(),
                paquete.getActualizadoEn()
        );
    }

    public DimensionesResponse toDimensionesResponse(Dimensiones dimensiones) {
        return new DimensionesResponse(
                dimensiones.getLargo(),
                dimensiones.getAncho(),
                dimensiones.getAlto()
        );
    }

    public Dimensiones toDimensiones(DimensionesRequest request) {
        return Dimensiones.builder()
                .largo(request.largo())
                .ancho(request.ancho())
                .alto(request.alto())
                .build();
    }
}
