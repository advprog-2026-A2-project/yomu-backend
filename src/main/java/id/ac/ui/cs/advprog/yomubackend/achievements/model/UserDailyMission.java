package id.ac.ui.cs.advprog.yomubackend.achievements.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserDailyMission — progress pelajar terhadap satu DailyMission pada satu hari.
 * Satu record per (userId, missionId, date).
 */
@Entity
@Table(
        name = "user_daily_missions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "mission_id", "date"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserDailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mission_id", nullable = false)
    private DailyMission mission;

    /** Tanggal misi ini berlaku — direset setiap hari. */
    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int currentProgress;

    @Column(nullable = false)
    private boolean isCompleted;

    @Column
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private boolean rewardClaimed;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ----------------------------------------------------------------
    // Domain Behaviour
    // ----------------------------------------------------------------

    /**
     * Perbarui progress berdasarkan nilai terbaru.
     * @param newProgress adalah progress dari user.
     * @return true jika misi baru saja selesai pada pemanggilan ini.
     */
    public boolean updateProgress(final int newProgress) {
        if (this.isCompleted) return false;

        this.currentProgress = Math.min(
                newProgress,
                this.mission.getRequirementTargetValue()
        );

        if (this.currentProgress >= this.mission.getRequirementTargetValue()) {
            this.isCompleted = true;
            this.completedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * Klaim reward XP setelah misi selesai.
     * Akan melempar exception jika misi belum selesai atau reward sudah diklaim.
     */
    public void claimReward() {
        if (!this.isCompleted) {
            throw new IllegalStateException("Misi belum selesai.");
        }
        if (this.rewardClaimed) {
            throw new IllegalStateException("Reward sudah diklaim sebelumnya.");
        }
        this.rewardClaimed = true;
    }
}
