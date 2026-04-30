package com.smartship.application.service;

import com.smartship.application.dto.auth.LoginRequest;
import com.smartship.application.dto.auth.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
