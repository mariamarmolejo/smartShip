package com.smartship.domain.state;

import com.smartship.domain.exception.TransicionNoPermitidaException;
import com.smartship.domain.model.EstadoPaquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("RecibidoHandler — transiciones desde RECIBIDO")
class RecibidoHandlerTest {

    private RecibidoHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RecibidoHandler();
    }

    @Test
    @DisplayName("RECIBIDO → EN_TRANSITO es válida")
    void transicion_RECIBIDO_a_EN_TRANSITO_esValida() {
        EstadoPaquete resultado = handler.transicionar(EstadoPaquete.EN_TRANSITO);

        assertThat(resultado).isEqualTo(EstadoPaquete.EN_TRANSITO);
    }

    @Test
    @DisplayName("RECIBIDO → ENTREGADO lanza excepción (R001 — transición prohibida)")
    void transicion_RECIBIDO_a_ENTREGADO_lanzaExcepcion() {
        assertThatThrownBy(() -> handler.transicionar(EstadoPaquete.ENTREGADO))
                .isInstanceOf(TransicionNoPermitidaException.class)
                .hasMessageContaining("RECIBIDO")
                .hasMessageContaining("ENTREGADO");
    }

    @Test
    @DisplayName("RECIBIDO → RECIBIDO lanza excepción (no se permite permanecer en el mismo estado)")
    void transicion_RECIBIDO_a_RECIBIDO_lanzaExcepcion() {
        assertThatThrownBy(() -> handler.transicionar(EstadoPaquete.RECIBIDO))
                .isInstanceOf(TransicionNoPermitidaException.class)
                .hasMessageContaining("RECIBIDO");
    }
}
