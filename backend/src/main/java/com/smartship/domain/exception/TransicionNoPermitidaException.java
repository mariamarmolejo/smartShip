package com.smartship.domain.exception;

import com.smartship.domain.model.EstadoPaquete;

public class TransicionNoPermitidaException extends RuntimeException {

    public TransicionNoPermitidaException(EstadoPaquete origen, EstadoPaquete destino) {
        super(String.format("Transición no permitida: %s → %s", origen, destino));
    }
}
