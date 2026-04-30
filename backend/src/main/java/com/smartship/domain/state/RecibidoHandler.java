package com.smartship.domain.state;

import com.smartship.domain.exception.TransicionNoPermitidaException;
import com.smartship.domain.model.EstadoPaquete;

public class RecibidoHandler implements EstadoHandler {

    @Override
    public EstadoPaquete transicionar(EstadoPaquete estadoDestino) {
        if (estadoDestino == EstadoPaquete.EN_TRANSITO) {
            return EstadoPaquete.EN_TRANSITO;
        }
        throw new TransicionNoPermitidaException(EstadoPaquete.RECIBIDO, estadoDestino);
    }
}
