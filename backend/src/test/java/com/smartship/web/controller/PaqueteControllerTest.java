package com.smartship.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartship.application.dto.paquete.CrearPaqueteRequest;
import com.smartship.application.dto.paquete.DimensionesRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PaqueteController — CRUD y validaciones")
class PaqueteControllerTest {

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

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("POST /api/paquetes — ADMINISTRADOR crea paquete exitosamente → 201")
    void crearPaquete_comoAdministrador_retorna201() throws Exception {
        CrearPaqueteRequest request = new CrearPaqueteRequest(
                1.5,
                new DimensionesRequest(10.0, 5.0, 3.0),
                "Juan Pérez"
        );

        mockMvc.perform(post("/api/paquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trackingId").exists())
                .andExpect(jsonPath("$.trackingId").value(org.hamcrest.Matchers.startsWith("PKG-")))
                .andExpect(jsonPath("$.estado").value("RECIBIDO"))
                .andExpect(jsonPath("$.destinatario").value("Juan Pérez"))
                .andExpect(jsonPath("$.peso").value(1.5));
    }

    @Test
    @WithMockUser(roles = "REPARTIDOR")
    @DisplayName("POST /api/paquetes — REPARTIDOR no puede crear paquetes → 403 (R005)")
    void crearPaquete_comoRepartidor_retorna403() throws Exception {
        CrearPaqueteRequest request = new CrearPaqueteRequest(
                1.5,
                new DimensionesRequest(10.0, 5.0, 3.0),
                "Juan Pérez"
        );

        mockMvc.perform(post("/api/paquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("POST /api/paquetes — peso = 0 → 400 (R007)")
    void crearPaquete_conPesoCero_retorna400() throws Exception {
        CrearPaqueteRequest request = new CrearPaqueteRequest(
                0.0,
                new DimensionesRequest(10.0, 5.0, 3.0),
                "Juan Pérez"
        );

        mockMvc.perform(post("/api/paquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("POST /api/paquetes — dimensiones con valor 0 → 400 (R008)")
    void crearPaquete_conDimensionesInvalidas_retorna400() throws Exception {
        CrearPaqueteRequest request = new CrearPaqueteRequest(
                1.5,
                new DimensionesRequest(0.0, 5.0, 3.0),
                "Juan Pérez"
        );

        mockMvc.perform(post("/api/paquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("POST /api/paquetes — destinatario vacío → 400 (R009)")
    void crearPaquete_sinDestinatario_retorna400() throws Exception {
        CrearPaqueteRequest request = new CrearPaqueteRequest(
                1.5,
                new DimensionesRequest(10.0, 5.0, 3.0),
                ""
        );

        mockMvc.perform(post("/api/paquetes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    @DisplayName("GET /api/paquetes — JWT válido → 200 con lista de paquetes")
    void listarPaquetes_conJwtValido_retorna200() throws Exception {
        mockMvc.perform(get("/api/paquetes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
