package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MilestoneType;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievements;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO response untuk data Achievement.
 * Digunakan untuk endpoint publik maupun admin.
 */
@Getter
public class AchievementResponse {

    private final String id;
    private final String achievementName;
    private final String achievementDescription;
    private final MilestoneType milestoneType;
    private final int milestoneTargetValue;
    private final String iconUrl;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    /** Buat response dari entitas Achievement. */
    public AchievementResponse(final Achievements achievement) {
        this.id = achievement.getId();
        this.achievementName = achievement.getAchievementName();
        this.achievementDescription = achievement.getAchievementDescription();
        this.milestoneType = achievement.getMilestoneType();
        this.milestoneTargetValue = achievement.getMilestoneTargetValue();
        this.iconUrl = achievement.getIconUrl();
        this.createdAt = achievement.getCreatedAt();
        this.updatedAt = achievement.getUpdatedAt();
    }
}
