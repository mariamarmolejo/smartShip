package com.smartship.domain.state;

import com.smartship.domain.exception.TransicionNoPermitidaException;
import com.smartship.domain.model.EstadoPaquete;

public class EnTransitoHandler implements EstadoHandler {

    @Override
    public EstadoPaquete transicionar(EstadoPaquete estadoDestino) {
        if (estadoDestino == EstadoPaquete.ENTREGADO) {
            return EstadoPaquete.ENTREGADO;
        }
        throw new TransicionNoPermitidaException(EstadoPaquete.EN_TRANSITO, estadoDestino);
    }
}
