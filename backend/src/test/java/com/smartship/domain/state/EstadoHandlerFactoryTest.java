package com.smartship.domain.state;

import com.smartship.domain.model.EstadoPaquete;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EstadoHandler.para() — fábrica estática de handlers")
class EstadoHandlerFactoryTest {

    @Test
    @DisplayName("para(RECIBIDO) devuelve RecibidoHandler")
    void para_RECIBIDO_devuelve_RecibidoHandler() {
        EstadoHandler handler = EstadoHandler.para(EstadoPaquete.RECIBIDO);

        assertThat(handler).isInstanceOf(RecibidoHandler.class);
    }

    @Test
    @DisplayName("para(EN_TRANSITO) devuelve EnTransitoHandler")
    void para_EN_TRANSITO_devuelve_EnTransitoHandler() {
        EstadoHandler handler = EstadoHandler.para(EstadoPaquete.EN_TRANSITO);

        assertThat(handler).isInstanceOf(EnTransitoHandler.class);
    }

    @Test
    @DisplayName("para(ENTREGADO) devuelve EntregadoHandler")
    void para_ENTREGADO_devuelve_EntregadoHandler() {
        EstadoHandler handler = EstadoHandler.para(EstadoPaquete.ENTREGADO);

        assertThat(handler).isInstanceOf(EntregadoHandler.class);
    }
}
