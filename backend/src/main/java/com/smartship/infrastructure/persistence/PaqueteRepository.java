package com.smartship.infrastructure.persistence;

import com.smartship.domain.model.Paquete;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaqueteRepository extends JpaRepository<Paquete, Long> {
    Optional<Paquete> findByTrackingId(String trackingId);
}
