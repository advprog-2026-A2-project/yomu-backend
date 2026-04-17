package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;

/**
 * Service contract for authentication operations.
 */
public interface AuthService {

    /**
     * Register a new user.
     *
     * @param request request payload for registration
     * @return authentication response
     */
    AuthResponse register(final RegisterRequest request);

    /**
     * Authenticate a user with identifier and password.
     *
     * @param request login request payload
     * @return authentication response
     */
    AuthResponse login(final LoginRequest request);

    /**
     * Authenticate a user with Google OAuth.
     *
     * @param idToken Google ID token
     * @return authentication response
     */
    AuthResponse loginWithGoogle(final String idToken);
}
