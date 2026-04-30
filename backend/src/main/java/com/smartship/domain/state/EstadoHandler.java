package com.smartship.domain.state;

import com.smartship.domain.model.EstadoPaquete;

public interface EstadoHandler {

    EstadoPaquete transicionar(EstadoPaquete estadoDestino);

    static EstadoHandler para(EstadoPaquete estado) {
        return switch (estado) {
            case RECIBIDO    -> new RecibidoHandler();
            case EN_TRANSITO -> new EnTransitoHandler();
            case ENTREGADO   -> new EntregadoHandler();
        };
    }
}
