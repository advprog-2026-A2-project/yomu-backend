package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MilestoneType;
import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MissionRequirementType;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievements;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievements;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementsRepo;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepo;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementsRepo;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepo;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Implementasi {@link UserProgressService}.
 *
 * <p>Pola desain: setiap event menghasilkan serangkaian upsert pada tabel
 * {@code user_achievements} dan {@code user_daily_missions}. Record dibuat
 * otomatis saat progress pertama kali dicatat (lazy initialization).</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserProgressServiceImpl implements UserProgressService {

    /** Repository lookup user (cross-module dependency yang sah: auth adalah fondasi). */
    private final UserRepository userRepository;

    private final AchievementsRepo achievementsRepo;
    private final UserAchievementsRepo userAchievementsRepo;
    private final DailyMissionRepo dailyMissionRepo;
    private final UserDailyMissionRepo userDailyMissionRepo;

    // ----------------------------------------------------------------
    // handleQuizCompleted
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public void handleQuizCompleted(
            final String username, final String kategori, final int score) {

        final User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.warn("[Achievement] User tidak ditemukan saat handle quiz: {}", username);
            return;
        }

        // 1. Achievement COMPLETE_READINGS — tambah 1 per kuis selesai
        updateAchievementProgress(user.getId(), MilestoneType.COMPLETE_READINGS, 1);

        // 2. Achievement QUIZ_ACCURACY — catat skor tertinggi sebagai progress
        //    (progress = skor, milestone = target akurasi misal 80)
        updateAchievementProgressAccuracy(user.getId(), score);

        // 3. Daily missions hari ini
        final boolean anyMissionJustCompleted =
                updateDailyMissionProgress(user, kategori, score);

        // 4. Jika ada daily mission yang baru selesai, trigger achievement COMPLETE_DAILY_MISSIONS
        if (anyMissionJustCompleted) {
            updateAchievementProgress(user.getId(), MilestoneType.COMPLETE_DAILY_MISSIONS, 1);
        }
    }

    // ----------------------------------------------------------------
    // handleJoinClan
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public void handleJoinClan(final String userId) {
        // JOIN_CLAN milestone target biasanya 1 (cukup bergabung sekali)
        updateAchievementProgress(userId, MilestoneType.JOIN_CLAN, 1);
    }

    // ----------------------------------------------------------------
    // handleClanPromotion
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public void handleClanPromotion(final String userId) {
        updateAchievementProgress(userId, MilestoneType.CLAN_PROMOTION, 1);
    }

    // ----------------------------------------------------------------
    // handleStreakDay
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public void handleStreakDay(final String username) {
        final User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            log.warn("[Achievement] User tidak ditemukan saat handle streak: {}", username);
            return;
        }
        updateAchievementProgress(user.getId(), MilestoneType.STREAK_DAYS, 1);
    }

    // ================================================================
    // Helper: achievement progress
    // ================================================================

    /**
     * Tambah progress (incremental) untuk semua achievement dengan tipe tertentu.
     * Record UserAchievements dibuat otomatis jika belum ada.
     */
    private void updateAchievementProgress(
            final String userId, final MilestoneType type, final int amount) {

        final List<Achievements> targets = achievementsRepo.findByMilestoneType(type);
        for (Achievements achievement : targets) {
            final UserAchievements ua = getOrCreateUserAchievement(userId, achievement);
            final boolean justCompleted = ua.addProgress(amount);
            userAchievementsRepo.save(ua);

            if (justCompleted) {
                log.info("[Achievement] User {} menyelesaikan achievement: {}",
                        userId, achievement.getAchievementName());
            }
        }
    }

    /**
     * Update progress achievement QUIZ_ACCURACY.
     * Menyimpan skor jika lebih tinggi dari progress saat ini
     * (semantik: "pernah capai akurasi X%").
     */
    private void updateAchievementProgressAccuracy(final String userId, final int score) {
        final List<Achievements> targets =
                achievementsRepo.findByMilestoneType(MilestoneType.QUIZ_ACCURACY);
        for (Achievements achievement : targets) {
            final UserAchievements ua = getOrCreateUserAchievement(userId, achievement);
            if (ua.isCompleted()) continue;

            // Hanya naikkan jika skor baru lebih tinggi dari yang tersimpan
            final int diff = score - ua.getCurrentProgress();
            if (diff > 0) {
                final boolean justCompleted = ua.addProgress(diff);
                userAchievementsRepo.save(ua);
                if (justCompleted) {
                    log.info("[Achievement] User {} mencapai akurasi achievement: {}",
                            userId, achievement.getAchievementName());
                }
            }
        }
    }

    /**
     * Cari atau buat record UserAchievements (lazy initialization).
     */
    private UserAchievements getOrCreateUserAchievement(
            final String userId, final Achievements achievement) {
        return userAchievementsRepo
                .findByUserIdAndAchievement(userId, achievement)
                .orElseGet(() -> {
                    UserAchievements newUa = UserAchievements.builder()
                            .userId(userId)
                            .achievement(achievement)
                            .currentProgress(0)
                            .isCompleted(false)
                            .isDisplayed(false)
                            .build();
                    return userAchievementsRepo.save(newUa);
                });
    }

    // ================================================================
    // Helper: daily mission progress
    // ================================================================

    /**
     * Perbarui semua UserDailyMission aktif milik user untuk hari ini.
     * Record dibuat otomatis jika belum ada.
     *
     * @return true jika ada minimal satu daily mission yang baru saja selesai
     */
    private boolean updateDailyMissionProgress(
            final User user, final String kategori, final int score) {

        final UUID userUuid = UUID.fromString(user.getId());
        final LocalDate today = LocalDate.now();
        final List<DailyMission> activeMissions = dailyMissionRepo.findByIsActiveTrue();
        boolean anyJustCompleted = false;

        for (DailyMission mission : activeMissions) {
            final UserDailyMission udm = getOrCreateUserDailyMission(userUuid, mission, today);
            if (udm.isCompleted()) continue;

            final int newProgress = calculateMissionProgress(
                    udm, mission, kategori, score);

            if (newProgress > udm.getCurrentProgress()) {
                final boolean justCompleted = udm.updateProgress(newProgress);
                userDailyMissionRepo.save(udm);
                if (justCompleted) {
                    anyJustCompleted = true;
                    log.info("[Achievement] User {} menyelesaikan daily mission: {}",
                            user.getUsername(), mission.getName());
                }
            }
        }
        return anyJustCompleted;
    }

    /**
     * Hitung nilai progress baru berdasarkan tipe syarat misi.
     *
     * @param udm      record progress saat ini
     * @param mission  definisi misi
     * @param kategori kategori bacaan yang baru diselesaikan
     * @param score    skor kuis
     * @return nilai progress baru (absolut, sesuai kontrak updateProgress)
     */
    private int calculateMissionProgress(
        final UserDailyMission udm,
        final DailyMission mission,
        final String kategori,
        final int score) {

        return switch (mission.getRequirementType()) {

            // Baca N bacaan hari ini — increment 1
            case COMPLETE_N_READINGS, COMPLETE_N_QUIZZES ->
                    udm.getCurrentProgress() + 1;

            // Kuis dengan akurasi >= N% — set 1 jika lolos, tetap 0 jika tidak
            case COMPLETE_QUIZ_ACCURACY ->
                    (score >= mission.getRequirementTargetValue())
                            ? udm.getCurrentProgress() + 1
                            : udm.getCurrentProgress();

            // Baca dari kategori tertentu — increment hanya jika kategori cocok
            case READ_CATEGORY ->
                    (mission.getRequirementCategory() != null
                            && mission.getRequirementCategory().equalsIgnoreCase(kategori))
                            ? udm.getCurrentProgress() + 1
                            : udm.getCurrentProgress();
        };
    }

    /**
     * Cari atau buat record UserDailyMission (lazy initialization).
     */
    private UserDailyMission getOrCreateUserDailyMission(
            final UUID userId, final DailyMission mission, final LocalDate date) {
        return userDailyMissionRepo
                .findByUserIdAndMissionAndDate(userId, mission, date)
                .orElseGet(() -> {
                    UserDailyMission newUdm = UserDailyMission.builder()
                            .userId(userId)
                            .mission(mission)
                            .date(date)
                            .currentProgress(0)
                            .isCompleted(false)
                            .rewardClaimed(false)
                            .build();
                    return userDailyMissionRepo.save(newUdm);
                });
    }
}
