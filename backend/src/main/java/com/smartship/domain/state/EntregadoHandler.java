package com.smartship.domain.state;

import com.smartship.domain.exception.TransicionNoPermitidaException;
import com.smartship.domain.model.EstadoPaquete;

public class EntregadoHandler implements EstadoHandler {

    @Override
    public EstadoPaquete transicionar(EstadoPaquete estadoDestino) {
        throw new TransicionNoPermitidaException(EstadoPaquete.ENTREGADO, estadoDestino);
    }
}
