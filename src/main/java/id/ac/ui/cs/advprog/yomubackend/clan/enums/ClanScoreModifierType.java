package id.ac.ui.cs.advprog.yomubackend.clan.enums;

import lombok.Getter;

@Getter
public enum ClanScoreModifierType {
    DOUBLE_XP("Double XP", 2.0),
    PRODUCTIVITY_BUFF("Productivity Buff", 1.2),
    LOW_ACCURACY_PENALTY("Low Accuracy Penalty", 0.8);

    private final String displayName;
    private final double multiplier;

    ClanScoreModifierType(String displayName, double multiplier) {
        this.displayName = displayName;
        this.multiplier = multiplier;
    }

}
