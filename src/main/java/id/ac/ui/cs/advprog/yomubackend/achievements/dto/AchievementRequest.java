package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MilestoneType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO request untuk membuat atau mengupdate Achievement oleh Admin.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementRequest {

    /** Nama achievement. */
    private String achievementName;

    /** Deskripsi achievement. */
    private String achievementDescription;

    /** Tipe milestone yang harus dipenuhi. */
    private MilestoneType milestoneType;

    /** Nilai target milestone (misal: 10 untuk "selesaikan 10 bacaan"). */
    private int milestoneTargetValue;

    /** URL ikon achievement (opsional). */
    private String iconUrl;
}
