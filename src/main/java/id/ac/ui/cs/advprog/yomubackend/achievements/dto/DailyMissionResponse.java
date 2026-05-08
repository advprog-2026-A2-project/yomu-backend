package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MissionRequirementType;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import lombok.Getter;

import java.util.UUID;

/**
 * DTO response untuk data DailyMission.
 */
@Getter
public class DailyMissionResponse {

    private final UUID id;
    private final String name;
    private final String description;
    private final MissionRequirementType requirementType;
    private final int requirementTargetValue;
    private final String requirementCategory;
    private final int rewardXp;
    private final boolean isActive;

    /** Buat response dari entitas DailyMission. */
    public DailyMissionResponse(final DailyMission mission) {
        this.id = mission.getId();
        this.name = mission.getName();
        this.description = mission.getDescription();
        this.requirementType = mission.getRequirementType();
        this.requirementTargetValue = mission.getRequirementTargetValue();
        this.requirementCategory = mission.getRequirementCategory();
        this.rewardXp = mission.getRewardXp();
        this.isActive = mission.isActive();
    }
}
