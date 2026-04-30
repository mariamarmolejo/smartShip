package com.smartship.application.service.impl;

import com.smartship.application.dto.paquete.CrearPaqueteRequest;
import com.smartship.application.dto.paquete.PaqueteResponse;
import com.smartship.application.mapper.PaqueteMapper;
import com.smartship.application.service.PaqueteService;
import com.smartship.domain.model.EstadoPaquete;
import com.smartship.domain.model.Paquete;
import com.smartship.infrastructure.persistence.PaqueteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaqueteServiceImpl implements PaqueteService {

    private final PaqueteRepository paqueteRepository;
    private final PaqueteMapper paqueteMapper;

    @Override
    @Transactional
    public PaqueteResponse crearPaquete(CrearPaqueteRequest request) {
        Paquete paquete = Paquete.builder()
                .trackingId(generarTrackingId())
                .peso(request.peso())
                .dimensiones(paqueteMapper.toDimensiones(request.dimensiones()))
                .destinatario(request.destinatario())
                .estado(EstadoPaquete.RECIBIDO)
                .build();

        return paqueteMapper.toResponse(paqueteRepository.save(paquete));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaqueteResponse> listarPaquetes() {
        return paqueteRepository.findAll()
                .stream()
                .map(paqueteMapper::toResponse)
                .toList();
    }

    private String generarTrackingId() {
        return "PKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
