package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Kontrak service untuk manajemen DailyMission.
 *
 * <p>Use case yang dilayani:
 * <ul>
 *   <li>Admin: CRUD daily mission, toggle aktif/nonaktif</li>
 *   <li>Pelajar: melihat misi aktif hari ini beserta progress</li>
 *   <li>Pelajar: mengklaim reward setelah misi selesai</li>
 * </ul>
 */
public interface DailyMissionService {

    /**
     * Admin membuat daily mission baru.
     *
     * @param request data misi
     * @return response misi yang dibuat
     */
    DailyMissionResponse createMission(DailyMissionRequest request);

    /**
     * Ambil semua daily mission (termasuk yang tidak aktif, untuk admin).
     *
     * @return list semua misi
     */
    List<DailyMissionResponse> getAllMissions();

    /**
     * Ambil semua daily mission yang sedang aktif.
     *
     * @return list misi aktif
     */
    List<DailyMissionResponse> getActiveMissions();

    /**
     * Admin mengupdate daily mission.
     *
     * @param missionId ID misi
     * @param request   data update
     * @return response misi yang diupdate
     */
    DailyMissionResponse updateMission(UUID missionId, DailyMissionRequest request);

    /**
     * Admin menghapus daily mission.
     *
     * @param missionId ID misi
     */
    void deleteMission(UUID missionId);

    /**
     * Admin mengaktifkan daily mission.
     *
     * @param missionId ID misi
     * @return response misi setelah diaktifkan
     */
    DailyMissionResponse activateMission(UUID missionId);

    /**
     * Admin menonaktifkan daily mission.
     *
     * @param missionId ID misi
     * @return response misi setelah dinonaktifkan
     */
    DailyMissionResponse deactivateMission(UUID missionId);

    // ----------------------------------------------------------------
    // Use case Pelajar
    // ----------------------------------------------------------------

    /**
     * Ambil progress misi harian user untuk hari ini.
     * Jika belum ada record, akan dibuat otomatis untuk setiap misi aktif.
     *
     * @param userId ID user (UUID)
     * @return list progress misi hari ini
     */
    List<UserDailyMissionResponse> getMyTodayMissions(UUID userId);

    /**
     * Pelajar mengklaim reward XP setelah misi selesai.
     *
     * @param userId          ID user (UUID)
     * @param userMissionId   ID record UserDailyMission
     * @return response setelah klaim
     */
    UserDailyMissionResponse claimReward(UUID userId, UUID userMissionId);
}
