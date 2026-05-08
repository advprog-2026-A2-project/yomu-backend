package id.ac.ui.cs.advprog.yomubackend.achievements.controller;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Controller REST untuk manajemen Daily Mission.
 *
 * <p>Endpoint:
 * <ul>
 *   <li>{@code /api/missions/admin/**} — Admin: CRUD misi</li>
 *   <li>{@code /api/missions/**}       — Pelajar: lihat & klaim</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class DailyMissionController {

    /** Service daily mission. */
    private final DailyMissionService dailyMissionService;

    /** Repository untuk resolve userId. */
    private final UserRepository userRepository;

    // ================================================================
    // Admin Endpoints
    // ================================================================

    /**
     * [Admin] Ambil semua daily mission (aktif maupun tidak).
     * GET /api/missions/admin
     */
    @GetMapping("/admin")
    public ResponseEntity<List<DailyMissionResponse>> getAllMissions() {
        return ResponseEntity.ok(dailyMissionService.getAllMissions());
    }

    /**
     * [Admin] Buat daily mission baru.
     * POST /api/missions/admin
     */
    @PostMapping("/admin")
    public ResponseEntity<DailyMissionResponse> createMission(
            @RequestBody final DailyMissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dailyMissionService.createMission(request));
    }

    /**
     * [Admin] Update daily mission.
     * PUT /api/missions/admin/{missionId}
     */
    @PutMapping("/admin/{missionId}")
    public ResponseEntity<DailyMissionResponse> updateMission(
            @PathVariable final UUID missionId,
            @RequestBody final DailyMissionRequest request) {
        return ResponseEntity.ok(dailyMissionService.updateMission(missionId, request));
    }

    /**
     * [Admin] Hapus daily mission.
     * DELETE /api/missions/admin/{missionId}
     */
    @DeleteMapping("/admin/{missionId}")
    public ResponseEntity<Void> deleteMission(@PathVariable final UUID missionId) {
        dailyMissionService.deleteMission(missionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * [Admin] Aktifkan daily mission.
     * PATCH /api/missions/admin/{missionId}/activate
     */
    @PatchMapping("/admin/{missionId}/activate")
    public ResponseEntity<DailyMissionResponse> activateMission(
            @PathVariable final UUID missionId) {
        return ResponseEntity.ok(dailyMissionService.activateMission(missionId));
    }

    /**
     * [Admin] Nonaktifkan daily mission.
     * PATCH /api/missions/admin/{missionId}/deactivate
     */
    @PatchMapping("/admin/{missionId}/deactivate")
    public ResponseEntity<DailyMissionResponse> deactivateMission(
            @PathVariable final UUID missionId) {
        return ResponseEntity.ok(dailyMissionService.deactivateMission(missionId));
    }

    // ================================================================
    // Pelajar Endpoints
    // ================================================================

    /**
     * [Pelajar] Lihat semua misi aktif hari ini beserta progress.
     * Format contoh: "Membaca Berita: 1/3"
     * GET /api/missions/today
     */
    @GetMapping("/today")
    public ResponseEntity<List<UserDailyMissionResponse>> getMyTodayMissions(
            final Principal principal) {
        final UUID userId = resolveUserUuid(principal);
        return ResponseEntity.ok(dailyMissionService.getMyTodayMissions(userId));
    }

    /**
     * [Pelajar] Ambil semua misi aktif (template, tanpa progress).
     * GET /api/missions/active
     */
    @GetMapping("/active")
    public ResponseEntity<List<DailyMissionResponse>> getActiveMissions() {
        return ResponseEntity.ok(dailyMissionService.getActiveMissions());
    }

    /**
     * [Pelajar] Klaim reward XP setelah misi selesai.
     * POST /api/missions/me/{userMissionId}/claim
     */
    @PostMapping("/me/{userMissionId}/claim")
    public ResponseEntity<?> claimReward(
            @PathVariable final UUID userMissionId,
            final Principal principal) {
        try {
            final UUID userId = resolveUserUuid(principal);
            return ResponseEntity.ok(
                    dailyMissionService.claimReward(userId, userMissionId));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // ================================================================
    // Helper
    // ================================================================

    private UUID resolveUserUuid(final Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("User belum login.");
        }
        final User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new NoSuchElementException(
                        "User tidak ditemukan: " + principal.getName()));
        return UUID.fromString(user.getId());
    }
}
