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

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final HttpSessionSecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

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
        
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(
                user.getUsername(), 
                null, 
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
            );
        
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
        
        // PENTING: Simpan SecurityContext ke session
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
}
