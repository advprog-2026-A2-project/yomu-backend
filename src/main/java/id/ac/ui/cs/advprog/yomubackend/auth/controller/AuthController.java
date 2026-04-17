package id.ac.ui.cs.advprog.yomubackend.auth.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public final class AuthController {

    /** Service for authentication operations. */
    private final AuthService authService;

    /** Repository used to lookup users. */
    private final UserRepository userRepository;

    /** Verifier used to validate Google OAuth ID tokens. */
    private final GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(
                            Collections.singletonList(
                                    "246704411302-hr4q3cb0u300318uvfp7q1b4lbjuvues.apps.googleusercontent.com"))
                    .build();

    /**
     * Register a new user.
     *
     * @param request registration request payload
     * @return authentication response
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody final RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    /**
     * Authenticate a user with username/email/phone and store the security context in session.
     *
     * @param request login request payload
     * @param session HTTP session to store the security context
     * @return authentication response
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody final LoginRequest request, final HttpSession session) {
        AuthResponse response = authService.login(request);

        User user = userRepository
                .findByUsername(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .or(() -> userRepository.findByPhoneNumber(request.getIdentifier()))
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        authenticateUserAndSaveSession(user, session);

        return ResponseEntity.ok(response);
    }

    /**
     * Log the current user out by invalidating the HTTP session.
     *
     * @param session HTTP session to invalidate
     * @return redirect view name
     */
    @GetMapping("/logout")
    public String logout(final HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            System.out.println(user.getUsername() + " is just logout.");
        }
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "login";
    }

    /**
     * Authenticate a user by Google token and store the security context in session.
     *
     * @param token Google ID token
     * @param session HTTP session to store the security context
     * @return authentication response
     */
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(
            @RequestParam("token") final String token, final HttpSession session) {
        AuthResponse response = authService.loginWithGoogle(token);

        String email;
        try {
            GoogleIdToken googleIdToken = verifier.verify(token);
            if (googleIdToken == null) {
                throw new IllegalArgumentException("Token Google tidak valid");
            }
            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            email = payload.getEmail();
        } catch (Exception e) {
            throw new IllegalArgumentException("Gagal ekstrak email dari token: " + e.getMessage());
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        authenticateUserAndSaveSession(user, session);

        return ResponseEntity.ok(response);
    }

    private void authenticateUserAndSaveSession(final User user, final HttpSession session) {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);
    }
}
