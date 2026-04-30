package com.smartship.infrastructure.config;

import com.smartship.domain.model.Rol;
import com.smartship.domain.model.Usuario;
import com.smartship.infrastructure.persistence.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        crearUsuarioSiNoExiste("admin", "admin123", Rol.ADMINISTRADOR);
        crearUsuarioSiNoExiste("repartidor", "rep123", Rol.REPARTIDOR);
        log.info("Usuarios de prueba disponibles: admin / admin123 (ADMINISTRADOR), repartidor / rep123 (REPARTIDOR)");
    }

    private void crearUsuarioSiNoExiste(String username, String password, Rol rol) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            usuarioRepository.save(Usuario.builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .rol(rol)
                    .build());
        }
    }
}
