package id.ac.ui.cs.advprog.yomubackend.auth.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
        new NetHttpTransport(), new GsonFactory())
        .setAudience(Collections.singletonList("246704411302-hr4q3cb0u300318uvfp7q1b4lbjuvues.apps.googleusercontent.com"))
        .build();

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        AuthResponse response = authService.login(request);

        // Buat authenticated session
        User user = userRepository.findByUsername(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .or(() -> userRepository.findByPhoneNumber(request.getIdentifier()))
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        // Satu baris kode ini penting karena ini dapat menyimpan SecurityContext ke session
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            System.out.println(user.getUsername() + " is just logout.");
        }
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "login";
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestParam("token") String token, HttpSession session) {
        AuthResponse response = authService.loginWithGoogle(token);

        // Ekstrak email dari token
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

        // Cari user untuk session
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        // Buat authenticated session seperti di login biasa
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        // PENTING: Simpan SecurityContext ke session
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return ResponseEntity.ok(response);
    }
}
