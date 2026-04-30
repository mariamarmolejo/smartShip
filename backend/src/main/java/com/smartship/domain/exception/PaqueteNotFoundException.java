package com.smartship.domain.exception;

public class PaqueteNotFoundException extends RuntimeException {

    public PaqueteNotFoundException(Long id) {
        super("Paquete no encontrado con ID: " + id);
    }
}
