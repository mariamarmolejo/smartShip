package com.smartship.domain.state;

import com.smartship.domain.exception.TransicionNoPermitidaException;
import com.smartship.domain.model.EstadoPaquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EntregadoHandler — ENTREGADO es estado terminal")
class EntregadoHandlerTest {

    private EntregadoHandler handler;

    @BeforeEach
    void setUp() {
        handler = new EntregadoHandler();
    }

    @ParameterizedTest(name = "ENTREGADO → {0} lanza excepción (R003 — estado terminal)")
    @EnumSource(EstadoPaquete.class)
    @DisplayName("Cualquier transición desde ENTREGADO lanza excepción")
    void transicion_desdeEntregado_siempreLanzaExcepcion(EstadoPaquete destino) {
        assertThatThrownBy(() -> handler.transicionar(destino))
                .isInstanceOf(TransicionNoPermitidaException.class)
                .hasMessageContaining("ENTREGADO");
    }
}
