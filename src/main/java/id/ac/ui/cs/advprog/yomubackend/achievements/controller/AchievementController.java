package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.AchievementService;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Controller REST untuk Modul Achievement.
 *
 * <p>Endpoint dikelompokkan dalam dua segmen:
 * <ul>
 *   <li>{@code /api/achievements/admin/**} — hanya untuk Admin</li>
 *   <li>{@code /api/achievements/**}       — untuk Pelajar yang sudah login</li>
 * </ul>
 * </p>
 *
 * <p>Otorisasi berbasis role dilakukan di SecurityConfig (TODO: tambahkan
 * {@code .hasRole("ADMIN")} untuk endpoint admin saat security diperketat).</p>
 */
@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
public class AchievementController {

    /** Service achievement. */
    private final AchievementService achievementService;

    /** Repository untuk resolve userId dari username principal. */
    private final UserRepository userRepository;

    // ================================================================
    // Admin Endpoints
    // ================================================================

    /**
     * [Admin] Ambil semua achievement yang terdaftar di sistem.
     * GET /api/achievements/admin
     */
    @GetMapping("/admin")
    public ResponseEntity<List<AchievementResponse>> getAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    /**
     * [Admin] Buat achievement baru.
     * POST /api/achievements/admin
     */
    @PostMapping("/admin")
    public ResponseEntity<AchievementResponse> createAchievement(
            @RequestBody final AchievementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(achievementService.createAchievement(request));
    }

    /**
     * [Admin] Update achievement berdasarkan ID.
     * PUT /api/achievements/admin/{achievementId}
     */
    @PutMapping("/admin/{achievementId}")
    public ResponseEntity<AchievementResponse> updateAchievement(
            @PathVariable final String achievementId,
            @RequestBody final AchievementRequest request) {
        return ResponseEntity.ok(
                achievementService.updateAchievement(achievementId, request));
    }

    /**
     * [Admin] Hapus achievement.
     * DELETE /api/achievements/admin/{achievementId}
     */
    @DeleteMapping("/admin/{achievementId}")
    public ResponseEntity<Void> deleteAchievement(
            @PathVariable final String achievementId) {
        achievementService.deleteAchievement(achievementId);
        return ResponseEntity.noContent().build();
    }

    // ================================================================
    // Pelajar Endpoints
    // ================================================================

    /**
     * [Pelajar] Ambil semua achievement (template dari sistem).
     * Digunakan untuk menampilkan daftar achievement yang bisa diselesaikan.
     * GET /api/achievements
     */
    @GetMapping
    public ResponseEntity<List<AchievementResponse>> listAllAchievements() {
        return ResponseEntity.ok(achievementService.getAllAchievements());
    }

    /**
     * [Pelajar] Ambil detail satu achievement.
     * GET /api/achievements/{achievementId}
     */
    @GetMapping("/{achievementId}")
    public ResponseEntity<AchievementResponse> getAchievement(
            @PathVariable final String achievementId) {
        return ResponseEntity.ok(achievementService.getAchievementById(achievementId));
    }

    /**
     * [Pelajar] Ambil semua progress achievement milik user yang sedang login.
     * Menampilkan progress termasuk yang belum selesai.
     * GET /api/achievements/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<UserAchievementResponse>> getMyAchievements(
            final Principal principal) {
        final String userId = resolveUserId(principal);
        return ResponseEntity.ok(achievementService.getMyAchievements(userId));
    }

    /**
     * [Pelajar] Lihat profil pelajar lain — hanya achievement yang ditampilkan.
     * GET /api/achievements/profile/{userId}
     */
    @GetMapping("/profile/{userId}")
    public ResponseEntity<List<UserAchievementResponse>> getDisplayedAchievements(
            @PathVariable final String userId) {
        return ResponseEntity.ok(achievementService.getDisplayedAchievements(userId));
    }

    /**
     * [Pelajar] Toggle tampil/sembunyikan achievement di profil publik.
     * PATCH /api/achievements/me/{userAchievementId}/toggle-display
     */
    @PatchMapping("/me/{userAchievementId}/toggle-display")
    public ResponseEntity<?> toggleDisplay(
            @PathVariable final String userAchievementId,
            final Principal principal) {
        try {
            final String userId = resolveUserId(principal);
            return ResponseEntity.ok(
                    achievementService.toggleDisplay(userId, userAchievementId));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ================================================================
    // Error handlers (global handler bisa ditambahkan di @ControllerAdvice)
    // ================================================================
    /**
     * Tangani IllegalArgumentException — kembalikan 403 Forbidden.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(final IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", ex.getMessage()));
    }
    // ================================================================
    // Helper
    // ================================================================

    /**
     * Ambil userId (UUID String) dari principal session.
     * Principal.getName() memberikan username, lalu di-resolve ke User.id.
     */
    private String resolveUserId(final Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("User belum login.");
        }
        final User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new NoSuchElementException(
                        "User tidak ditemukan: " + principal.getName()));
        return user.getId();
    }
}
