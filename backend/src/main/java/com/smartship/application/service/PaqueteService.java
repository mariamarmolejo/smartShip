package com.smartship.application.service;

import com.smartship.application.dto.paquete.CrearPaqueteRequest;
import com.smartship.application.dto.paquete.PaqueteResponse;

import java.util.List;

public interface PaqueteService {
    PaqueteResponse crearPaquete(CrearPaqueteRequest request);
    List<PaqueteResponse> listarPaquetes();
}
