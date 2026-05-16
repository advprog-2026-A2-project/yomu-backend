package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanTierMovementType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ClanSeasonResultDto {

    private final LocalDateTime processedAt;
    private final int promotedCount;
    private final int demotedCount;
    private final int stayedCount;
    private final List<ClanTierMovementDto> movements;

    public ClanSeasonResultDto(
            LocalDateTime processedAt,
            List<ClanTierMovementDto> movements) {
        this.processedAt = processedAt;
        this.movements = List.copyOf(movements);
        this.promotedCount = countMovement(ClanTierMovementType.PROMOTED);
        this.demotedCount = countMovement(ClanTierMovementType.DEMOTED);
        this.stayedCount = countMovement(ClanTierMovementType.STAYED);
    }

    private int countMovement(ClanTierMovementType movementType) {
        return (int) movements.stream()
                .filter(movement -> movement.getMovementType() == movementType)
                .count();
    }
}
