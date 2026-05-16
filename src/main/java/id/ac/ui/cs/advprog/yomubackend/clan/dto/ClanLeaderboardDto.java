package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import java.util.List;
import lombok.Getter;

@Getter
public class ClanLeaderboardDto {

    private final TierType tierType;
    private final Long currentClanId;
    private final String currentClanName;
    private final List<LeaderboardEntryDto> entries;

    public ClanLeaderboardDto(
            TierType tierType,
            Long currentClanId,
            String currentClanName,
            List<LeaderboardEntryDto> entries) {
        this.tierType = tierType;
        this.currentClanId = currentClanId;
        this.currentClanName = currentClanName;
        this.entries = entries;
    }
}
