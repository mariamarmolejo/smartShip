package com.smartship.application.service.impl;

import com.smartship.application.dto.auth.LoginRequest;
import com.smartship.application.dto.auth.LoginResponse;
import com.smartship.application.service.AuthService;
import com.smartship.domain.model.Usuario;
import com.smartship.infrastructure.persistence.UsuarioRepository;
import com.smartship.infrastructure.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        Usuario usuario = usuarioRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + request.username()));

        String token = jwtService.generateToken(usuario.getUsername(), usuario.getRol());
        return new LoginResponse(token, usuario.getRol().name());
    }
}
