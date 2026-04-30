package com.smartship.domain.state;

import com.smartship.domain.exception.TransicionNoPermitidaException;
import com.smartship.domain.model.EstadoPaquete;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("EnTransitoHandler — transiciones desde EN_TRANSITO")
class EnTransitoHandlerTest {

    private EnTransitoHandler handler;

    @BeforeEach
    void setUp() {
        handler = new EnTransitoHandler();
    }

    @Test
    @DisplayName("EN_TRANSITO → ENTREGADO es válida (R002)")
    void transicion_EN_TRANSITO_a_ENTREGADO_esValida() {
        EstadoPaquete resultado = handler.transicionar(EstadoPaquete.ENTREGADO);

        assertThat(resultado).isEqualTo(EstadoPaquete.ENTREGADO);
    }

    @Test
    @DisplayName("EN_TRANSITO → RECIBIDO lanza excepción (R004 — transición inversa prohibida)")
    void transicion_EN_TRANSITO_a_RECIBIDO_lanzaExcepcion() {
        assertThatThrownBy(() -> handler.transicionar(EstadoPaquete.RECIBIDO))
                .isInstanceOf(TransicionNoPermitidaException.class)
                .hasMessageContaining("EN_TRANSITO")
                .hasMessageContaining("RECIBIDO");
    }

    @Test
    @DisplayName("EN_TRANSITO → EN_TRANSITO lanza excepción (no se permite permanecer en el mismo estado)")
    void transicion_EN_TRANSITO_a_EN_TRANSITO_lanzaExcepcion() {
        assertThatThrownBy(() -> handler.transicionar(EstadoPaquete.EN_TRANSITO))
                .isInstanceOf(TransicionNoPermitidaException.class)
                .hasMessageContaining("EN_TRANSITO");
    }
}
