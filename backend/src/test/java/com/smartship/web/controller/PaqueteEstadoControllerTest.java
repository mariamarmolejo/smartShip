package com.smartship.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartship.application.dto.paquete.ActualizarEstadoRequest;
import com.smartship.domain.model.Dimensiones;
import com.smartship.domain.model.EstadoPaquete;
import com.smartship.domain.model.Paquete;
import com.smartship.infrastructure.persistence.PaqueteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PATCH /api/paquetes/{id}/estado — máquina de estados y RBAC")
class PaqueteEstadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaqueteRepository paqueteRepository;

    @BeforeEach
    void limpiarPaquetes() {
        paqueteRepository.deleteAll();
    }

    // ── Transiciones válidas ──────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("RECIBIDO → EN_TRANSITO → 200 (R002 — transición válida)")
    void actualizarEstado_RECIBIDO_a_EN_TRANSITO_retorna200() throws Exception {
        Paquete paquete = crearPaqueteConEstado(EstadoPaquete.RECIBIDO);

        mockMvc.perform(patch("/api/paquetes/{id}/estado", paquete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.EN_TRANSITO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_TRANSITO"))
                .andExpect(jsonPath("$.id").value(paquete.getId()));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("EN_TRANSITO → ENTREGADO → 200 (R002 — transición válida)")
    void actualizarEstado_EN_TRANSITO_a_ENTREGADO_retorna200() throws Exception {
        Paquete paquete = crearPaqueteConEstado(EstadoPaquete.EN_TRANSITO);

        mockMvc.perform(patch("/api/paquetes/{id}/estado", paquete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.ENTREGADO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
    }

    // ── Transiciones prohibidas ───────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("RECIBIDO → ENTREGADO → 422 (R001 — transición prohibida crítica)")
    void actualizarEstado_RECIBIDO_a_ENTREGADO_retorna422() throws Exception {
        Paquete paquete = crearPaqueteConEstado(EstadoPaquete.RECIBIDO);

        mockMvc.perform(patch("/api/paquetes/{id}/estado", paquete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.ENTREGADO))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("RECIBIDO")))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("ENTREGADO")));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("EN_TRANSITO → RECIBIDO → 422 (R004 — transición inversa prohibida)")
    void actualizarEstado_EN_TRANSITO_a_RECIBIDO_retorna422() throws Exception {
        Paquete paquete = crearPaqueteConEstado(EstadoPaquete.EN_TRANSITO);

        mockMvc.perform(patch("/api/paquetes/{id}/estado", paquete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.RECIBIDO))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("ENTREGADO → RECIBIDO → 422 (R003 — estado terminal)")
    void actualizarEstado_ENTREGADO_a_cualquier_estado_retorna422() throws Exception {
        Paquete paquete = crearPaqueteConEstado(EstadoPaquete.ENTREGADO);

        mockMvc.perform(patch("/api/paquetes/{id}/estado", paquete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.RECIBIDO))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value(
                        org.hamcrest.Matchers.containsString("ENTREGADO")));
    }

    // ── Errores de infraestructura ────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("Paquete inexistente → 404")
    void actualizarEstado_paqueteInexistente_retorna404() throws Exception {
        mockMvc.perform(patch("/api/paquetes/{id}/estado", 99999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.EN_TRANSITO))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // ── RBAC ─────────────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "REPARTIDOR")
    @DisplayName("REPARTIDOR puede actualizar estado → 200 (R006)")
    void actualizarEstado_comoRepartidor_retorna200() throws Exception {
        Paquete paquete = crearPaqueteConEstado(EstadoPaquete.RECIBIDO);

        mockMvc.perform(patch("/api/paquetes/{id}/estado", paquete.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.EN_TRANSITO))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_TRANSITO"));
    }

    @Test
    @DisplayName("Request sin autenticación → 401")
    void actualizarEstado_sinAutenticacion_retorna401() throws Exception {
        mockMvc.perform(patch("/api/paquetes/{id}/estado", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new ActualizarEstadoRequest(EstadoPaquete.EN_TRANSITO))))
                .andExpect(status().isUnauthorized());
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Paquete crearPaqueteConEstado(EstadoPaquete estado) {
        return paqueteRepository.save(Paquete.builder()
                .trackingId("PKG-TEST-" + System.nanoTime())
                .peso(1.5)
                .dimensiones(new Dimensiones(10.0, 5.0, 3.0))
                .destinatario("Destinatario de Prueba")
                .estado(estado)
                .build());
    }
}
