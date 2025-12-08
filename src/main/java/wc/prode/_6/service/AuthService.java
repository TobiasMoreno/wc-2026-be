package wc.prode._6.service;

import wc.prode._6.dto.request.LoginRequest;
import wc.prode._6.dto.request.RegisterRequest;
import wc.prode._6.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

