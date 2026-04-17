package id.ac.ui.cs.advprog.yomubackend.achievements.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * UserAchievement — progress seorang pelajar terhadap satu Achievement.
 * Dibuat otomatis oleh sistem saat pelajar mulai membuat progres.
 */
@Entity
@Table(
    name = "user_achievements",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserAchievements {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievements achievement;

    @Column(nullable = false)
    private int currentProgress;

    @Column(nullable = false)
    private boolean isCompleted;

    @Column
    private LocalDateTime completedAt;

    @Column(nullable = false)
    private boolean isDisplayed;

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
     * Tambah progress. Tandai selesai jika target sudah tercapai.
     * @return true jika achievement baru saja selesai pada pemanggilan ini
     */
    public boolean addProgress(int amount) {
        if (this.isCompleted) return false;

        this.currentProgress = Math.min(
                this.currentProgress + amount,
                this.achievement.getMilestoneTargetValue()
        );

        if (this.achievement.isSatisfiedBy(this.currentProgress)) {
            this.isCompleted = true;
            this.completedAt = LocalDateTime.now();
            return true; // baru saja selesai
        }
        return false;
    }

    /**
     * Pelajar memilih/membatalkan tampil achievement di profil publik.
     * Hanya achievement yang sudah selesai yang bisa ditampilkan.
     */
    public void toggleDisplay() {
        if (!this.isCompleted) {
            throw new IllegalStateException(
                    "Hanya achievement yang selesai yang dapat ditampilkan di profil."
            );
        }
        this.isDisplayed = !this.isDisplayed;
    }
}
