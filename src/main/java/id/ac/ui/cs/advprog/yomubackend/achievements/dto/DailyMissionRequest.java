package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MissionRequirementType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO request untuk membuat atau mengupdate DailyMission oleh Admin.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyMissionRequest {

    /** Nama misi. */
    private String name;

    /** Deskripsi misi. */
    private String description;

    /** Tipe syarat misi. */
    private MissionRequirementType requirementType;

    /** Nilai target syarat (misal: 3 untuk "baca 3 bacaan"). */
    private int requirementTargetValue;

    /**
     * Kategori bacaan (opsional).
     * Hanya relevan jika {@code requirementType == READ_CATEGORY}.
     */
    private String requirementCategory;

    /** XP reward yang diterima pelajar setelah misi selesai dan diklaim. */
    private int rewardXp;

    /** Apakah misi langsung aktif setelah dibuat. */
    private boolean isActive;
}
