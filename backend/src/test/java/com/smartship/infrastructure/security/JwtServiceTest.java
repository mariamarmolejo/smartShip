package com.smartship.infrastructure.security;

import com.smartship.domain.model.Rol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtService — generación y validación de tokens")
class JwtServiceTest {

    // El mismo secret configurado en application.yml
    private static final String SECRET =
            "c21hcnRzaGlwLXNlY3JldC1rZXktZm9yLWp3dC1hdXRoZW50aWNhdGlvbi0yMDI0";
    private static final long EXPIRATION = 86_400_000L; // 24 horas

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRATION);
    }

    @Test
    @DisplayName("generateToken + extractUsername: el username extraído coincide con el original")
    void generateToken_extractUsername_roundtrip() {
        String token = jwtService.generateToken("admin", Rol.ADMINISTRADOR);

        assertThat(jwtService.extractUsername(token)).isEqualTo("admin");
    }

    @Test
    @DisplayName("generateToken + extractRol: el rol extraído coincide con el original")
    void generateToken_extractRol_roundtrip() {
        String token = jwtService.generateToken("repartidor", Rol.REPARTIDOR);

        assertThat(jwtService.extractRol(token)).isEqualTo("REPARTIDOR");
    }

    @Test
    @DisplayName("isTokenValid: devuelve true para un token recién generado")
    void isTokenValid_conTokenValido_devuelveTrue() {
        String token = jwtService.generateToken("admin", Rol.ADMINISTRADOR);
        UserDetails userDetails = buildUserDetails("admin", "ROLE_ADMINISTRADOR");

        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid: devuelve false si el username no coincide")
    void isTokenValid_conUsernameDiferente_devuelveFalse() {
        String token = jwtService.generateToken("admin", Rol.ADMINISTRADOR);
        UserDetails userDetails = buildUserDetails("otro-usuario", "ROLE_ADMINISTRADOR");

        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    @DisplayName("extractUsername: lanza excepción con un token malformado")
    void extractUsername_conTokenInvalido_lanzaExcepcion() {
        assertThatThrownBy(() -> jwtService.extractUsername("token.invalido.aqui"))
                .isInstanceOf(Exception.class);
    }

    @Test
    @DisplayName("Token expirado: isTokenValid devuelve false")
    void isTokenValid_conTokenExpirado_devuelveFalse() {
        JwtService serviceConExpiradoInmediato = new JwtService(SECRET, -1000L);
        String tokenExpirado = serviceConExpiradoInmediato.generateToken("admin", Rol.ADMINISTRADOR);
        UserDetails userDetails = buildUserDetails("admin", "ROLE_ADMINISTRADOR");

        assertThat(serviceConExpiradoInmediato.isTokenValid(tokenExpirado, userDetails)).isFalse();
    }

    private UserDetails buildUserDetails(String username, String role) {
        return User.builder()
                .username(username)
                .password("password")
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .build();
    }
}
