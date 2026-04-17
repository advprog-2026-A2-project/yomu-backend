package id.ac.ui.cs.advprog.yomubackend.auth.service;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.AddIdentityRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Bakal error kalau lo belum punya bean PasswordEncoder di SecurityConfig

    @Override
    public void updateProfile(String currentUsername, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new IllegalArgumentException("Username sudah terpakai");
            }
            user.setUsername(request.getUsername());
        }

        if (request.getDisplayName() != null && !request.getDisplayName().isBlank()) {
            user.setDisplayName(request.getDisplayName());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*\\d).{10,}$")) {
                throw new IllegalArgumentException("Password min 10 karakter, ada huruf besar dan angka.");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);
    }

    @Override
    public void addIdentity(String currentUsername, AddIdentityRequest request) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        userRepository.save(user);
    }

    @Override
    public void deleteAccount(String currentUsername) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("User tidak ditemukan"));
        userRepository.delete(user);
    }
}