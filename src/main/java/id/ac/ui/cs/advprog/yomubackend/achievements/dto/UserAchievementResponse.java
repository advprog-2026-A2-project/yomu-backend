package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievements;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO response untuk progress achievement seorang pelajar.
 * Digunakan untuk halaman profil dan daftar achievement.
 */
@Getter
public class UserAchievementResponse {

    private final String id;
    private final AchievementResponse achievement;
    private final int currentProgress;
    private final boolean isCompleted;
    private final boolean isDisplayed;
    private final LocalDateTime completedAt;

    /** Buat response dari entitas UserAchievements. */
    public UserAchievementResponse(final UserAchievements userAchievement) {
        this.id = userAchievement.getId();
        this.achievement = new AchievementResponse(userAchievement.getAchievement());
        this.currentProgress = userAchievement.getCurrentProgress();
        this.isCompleted = userAchievement.isCompleted();
        this.isDisplayed = userAchievement.isDisplayed();
        this.completedAt = userAchievement.getCompletedAt();
    }
}
