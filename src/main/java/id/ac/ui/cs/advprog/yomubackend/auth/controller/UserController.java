package id.ac.ui.cs.advprog.yomubackend.auth.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.dto.AddIdentityRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomubackend.auth.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Helper untuk ambil username yang lagi login dari session (SecurityContext)
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new IllegalStateException("User belum login");
        }
        return auth.getName();
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileRequest request) {
        try {
            userService.updateProfile(getCurrentUsername(), request);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Profil berhasil diupdate"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/add-identity")
    public ResponseEntity<?> addIdentity(@RequestBody AddIdentityRequest request) {
        try {
            userService.addIdentity(getCurrentUsername(), request);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Identitas berhasil ditambah"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteAccount(HttpSession session) {
        try {
            userService.deleteAccount(getCurrentUsername());
            // Bersihin session karena akun udah dihapus
            SecurityContextHolder.clearContext();
            session.invalidate();
            return ResponseEntity.ok(Map.of("status", "success", "message", "Akun berhasil dihapus permanen"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
}