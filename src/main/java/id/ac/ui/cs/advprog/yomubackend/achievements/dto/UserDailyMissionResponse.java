package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO response untuk progress daily mission seorang pelajar.
 * Menampilkan informasi misi beserta progress hari ini, contoh: "Membaca Berita: 1/3".
 */
@Getter
public class UserDailyMissionResponse {

    private final UUID id;
    private final DailyMissionResponse mission;
    private final LocalDate date;
    private final int currentProgress;
    private final boolean isCompleted;
    private final boolean rewardClaimed;
    private final LocalDateTime completedAt;

    /** Buat response dari entitas UserDailyMission. */
    public UserDailyMissionResponse(final UserDailyMission udm) {
        this.id = udm.getId();
        this.mission = new DailyMissionResponse(udm.getMission());
        this.date = udm.getDate();
        this.currentProgress = udm.getCurrentProgress();
        this.isCompleted = udm.isCompleted();
        this.rewardClaimed = udm.isRewardClaimed();
        this.completedAt = udm.getCompletedAt();
    }
}
