package id.ac.ui.cs.advprog.yomubackend.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import java.util.Collections;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Default implementation of {@link AuthService}.
 */
@Service
@RequiredArgsConstructor
public final class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Daftar Email Admin
    private final List<String> adminEmails = Arrays.asList(
            "syahrousan@gmail.com",
            "mernawatispeed08@gmail.co"
    );

    private final GoogleIdTokenVerifier verifier =
            new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(
                            Collections.singletonList(
                                    "246704411302-hr4q3cb0u300318uvfp7q1b4lbjuvues.apps.googleusercontent.com"))
                    .build();

    @Override
    public AuthResponse register(final RegisterRequest request) {
        if (request.getEmail() == null && request.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Email atau nomor HP harus diisi");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username sudah dipakai");
        }
        if (request.getEmail() != null && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email sudah terdaftar");
        }
        if (request.getPhoneNumber() != null
                && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Nomor HP sudah terdaftar");
        }

        final User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDisplayName(request.getDisplayName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Cek Role Admin sebelum save
        if (user.getEmail() != null && adminEmails.contains(user.getEmail())) {
            user.setRole("ADMIN");
        } else {
            user.setRole("PELAJAR");
        }

        final User saved = userRepository.save(user);

        return new AuthResponse(
                "success",
                saved.getId(),
                saved.getUsername(),
                saved.getRole(),
                "Registrasi berhasil"
        );
    }

    @Override
    public AuthResponse login(final LoginRequest request) {
        final User user = userRepository.findByUsername(request.getIdentifier())
                .or(() -> userRepository.findByEmail(request.getIdentifier()))
                .or(() -> userRepository.findByPhoneNumber(request.getIdentifier()))
                .orElseThrow(
                        () -> new IllegalArgumentException("Akun tidak ditemukan atau password salah"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Akun tidak ditemukan atau password salah");
        }

        return new AuthResponse(
                "success", user.getId(), user.getUsername(), user.getRole(), "Login berhasil");
    }

    @Override
    public AuthResponse loginWithGoogle(final String idToken) {
        try {
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                throw new IllegalArgumentException("Token Google tidak valid");
            }

            GoogleIdToken.Payload payload = googleIdToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setUsername(email);
                newUser.setEmail(email);
                newUser.setDisplayName(name);
                newUser.setPassword("");

                // PENTING: Cek Admin juga pas pertama kali login Google
                if (email != null && adminEmails.contains(email)) {
                    newUser.setRole("ADMIN");
                } else {
                    newUser.setRole("PELAJAR");
                }

                return userRepository.save(newUser);
            });

            return new AuthResponse(
                    "success", user.getId(), user.getUsername(), user.getRole(), "Login Google berhasil");
        } catch (Exception e) {
            throw new IllegalArgumentException("Gagal verifikasi token Google: " + e.getMessage());
        }
    }
}