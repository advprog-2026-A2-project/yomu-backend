package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievements;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository untuk entitas UserAchievements. */
@Repository
public interface UserAchievementsRepo extends JpaRepository<UserAchievements, String> {

    /**
     * Ambil semua progress achievement milik satu user.
     *
     * @param userId ID user
     * @return list progress achievement user
     */
    List<UserAchievements> findByUserId(String userId);

    /**
     * Ambil progress untuk satu achievement spesifik milik user.
     * Digunakan untuk upsert progress agar tidak duplikat.
     *
     * @param userId        ID user
     * @param achievement   entitas achievement
     * @return Optional progress
     */
    Optional<UserAchievements> findByUserIdAndAchievement(String userId, Achievements achievement);

    /**
     * Ambil semua achievement yang sudah selesai dan dipilih untuk ditampilkan di profil.
     *
     * @param userId      ID user
     * @param isCompleted apakah ditampilkan
     * @return list achievement yang ditampilkan
     */
    List<UserAchievements> findByUserIdAndIsCompleted(String userId, boolean isCompleted);

    /**
     * Ambil achievement yang sudah selesai dan dipilih ditampilkan di profil publik.
     *
     * @param userId      ID user
     * @param isCompleted filter completed
     * @param isDisplayed filter ditampilkan
     * @return list
     */
    List<UserAchievements> findByUserIdAndIsCompletedAndIsDisplayed(
            String userId, boolean isCompleted, boolean isDisplayed);
}