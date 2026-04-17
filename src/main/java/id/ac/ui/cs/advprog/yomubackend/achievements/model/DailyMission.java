package id.ac.ui.cs.advprog.yomubackend.achievements.model;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MissionRequirementType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DailyMission — template misi harian yang dibuat oleh Admin.
 * Sistem akan merotasi misi aktif setiap hari.
 */
@Entity
@Table(name = "daily_missions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionRequirementType requirementType;

    /** Target nilai (misal: 3 untuk "baca 3 bacaan") */
    @Column(nullable = false)
    private int requirementTargetValue;

    /** Opsional: filter kategori bacaan, relevan untuk READ_CATEGORY */
    @Column
    private String requirementCategory;

    /** XP reward yang diterima pelajar setelah misi selesai dan diklaim */
    @Column(nullable = false)
    private int rewardXp;

    /** Apakah misi ini sedang aktif (bisa dirotasi) */
    @Column(nullable = false)
    private boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Update DailyMission oleh Admin */
    public void updateDetails(String name, String description, int rewardXp,
                              MissionRequirementType requirementType,
                              int requirementTargetValue, String requirementCategory) {
        this.name = name;
        this.description = description;
        this.rewardXp = rewardXp;
        this.requirementType = requirementType;
        this.requirementTargetValue = requirementTargetValue;
        this.requirementCategory = requirementCategory;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}