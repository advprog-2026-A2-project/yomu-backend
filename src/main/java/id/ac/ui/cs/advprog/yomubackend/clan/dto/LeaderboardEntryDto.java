package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import java.util.List;
import lombok.Getter;

@Getter
public class LeaderboardEntryDto {

    private final int rank;
    private final Long clanId;
    private final String clanName;
    private final TierType tierType;
    private final int memberCount;
    private final double score;
    private final boolean currentUserClan;
    private final double scoreMultiplier;
    private final List<String> activeScoreModifiers;

    public LeaderboardEntryDto(
            int rank,
            Long clanId,
            String clanName,
            TierType tierType,
            int memberCount,
            double score,
            boolean currentUserClan) {
        this(rank, clanId, clanName, tierType, memberCount, score,
                currentUserClan, 1.0, List.of());
    }

    public LeaderboardEntryDto(
            int rank,
            Long clanId,
            String clanName,
            TierType tierType,
            int memberCount,
            double score,
            boolean currentUserClan,
            double scoreMultiplier,
            List<String> activeScoreModifiers) {
        this.rank = rank;
        this.clanId = clanId;
        this.clanName = clanName;
        this.tierType = tierType;
        this.memberCount = memberCount;
        this.score = score;
        this.currentUserClan = currentUserClan;
        this.scoreMultiplier = scoreMultiplier;
        this.activeScoreModifiers = List.copyOf(activeScoreModifiers);
    }
}
