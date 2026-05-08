package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;

import java.util.List;

/**
 * Kontrak service untuk manajemen Achievement.
 *
 * <p>Use case yang dilayani:
 * <ul>
 *   <li>Admin: CRUD achievement</li>
 *   <li>Pelajar: melihat daftar achievement, toggle tampil di profil</li>
 *   <li>Pelajar: melihat profil pelajar lain (achievement yang ditampilkan)</li>
 * </ul>
 */
public interface AchievementService {

    /**
     * Admin membuat achievement baru.
     *
     * @param request data achievement
     * @return response achievement yang dibuat
     */
    AchievementResponse createAchievement(AchievementRequest request);

    /**
     * Ambil semua achievement yang tersedia di sistem.
     *
     * @return list semua achievement
     */
    List<AchievementResponse> getAllAchievements();

    /**
     * Ambil achievement berdasarkan ID.
     *
     * @param achievementId ID achievement
     * @return response achievement
     */
    AchievementResponse getAchievementById(String achievementId);

    /**
     * Admin mengupdate detail achievement (nama, deskripsi, ikon).
     *
     * @param achievementId ID achievement
     * @param request       data update
     * @return response achievement yang diupdate
     */
    AchievementResponse updateAchievement(String achievementId, AchievementRequest request);

    /**
     * Admin menghapus achievement.
     *
     * @param achievementId ID achievement
     */
    void deleteAchievement(String achievementId);

    // ----------------------------------------------------------------
    // Use case Pelajar
    // ----------------------------------------------------------------

    /**
     * Ambil semua progress achievement milik pelajar yang sedang login.
     *
     * @param userId ID user
     * @return list progress achievement user
     */
    List<UserAchievementResponse> getMyAchievements(String userId);

    /**
     * Ambil achievement yang ditampilkan di profil publik seorang pelajar.
     * Digunakan saat pelajar lain melihat profil.
     *
     * @param userId ID user yang profilnya dilihat
     * @return list achievement yang ditampilkan
     */
    List<UserAchievementResponse> getDisplayedAchievements(String userId);

    /**
     * Pelajar toggle tampil/sembunyikan achievement di profil publik.
     * Hanya achievement yang sudah selesai yang boleh ditampilkan.
     *
     * @param userId            ID user
     * @param userAchievementId ID record UserAchievements
     * @return response setelah toggle
     */
    UserAchievementResponse toggleDisplay(String userId, String userAchievementId);
}
