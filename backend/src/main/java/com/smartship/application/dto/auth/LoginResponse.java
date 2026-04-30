package com.smartship.application.dto.auth;

public record LoginResponse(
        String token,
        String rol
) {}
