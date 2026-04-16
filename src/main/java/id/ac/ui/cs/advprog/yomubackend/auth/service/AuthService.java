package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse loginWithGoogle(String idToken);
}
