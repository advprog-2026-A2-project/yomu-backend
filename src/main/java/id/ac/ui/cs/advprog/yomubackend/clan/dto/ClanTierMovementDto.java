package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanTierMovementType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import lombok.Getter;

@Getter
public class ClanTierMovementDto {

    private final Long clanId;
    private final String clanName;
    private final TierType previousTier;
    private final TierType newTier;
    private final int finalRank;
    private final double finalScore;
    private final ClanTierMovementType movementType;

    public ClanTierMovementDto(
            Long clanId,
            String clanName,
            TierType previousTier,
            TierType newTier,
            int finalRank,
            double finalScore,
            ClanTierMovementType movementType) {
        this.clanId = clanId;
        this.clanName = clanName;
        this.previousTier = previousTier;
        this.newTier = newTier;
        this.finalRank = finalRank;
        this.finalScore = finalScore;
        this.movementType = movementType;
    }
}
