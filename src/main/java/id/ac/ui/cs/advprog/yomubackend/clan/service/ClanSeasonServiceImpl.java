package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanSeasonResultDto;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanTierMovementDto;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanTierMovementType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanRankingService.RankedClan;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClanSeasonServiceImpl implements ClanSeasonService {

    private static final int PROMOTION_SLOTS = 1;
    private static final int DEMOTION_SLOTS = 1;
    private static final int MINIMUM_CLANS_FOR_MOVEMENT = 2;

    private final ClanRepository clanRepository;
    private final ClanRankingService clanRankingService;

    public ClanSeasonServiceImpl(
            ClanRepository clanRepository,
            ClanRankingService clanRankingService) {
        this.clanRepository = clanRepository;
        this.clanRankingService = clanRankingService;
    }

    @Override
    @Transactional
    public ClanSeasonResultDto processEndOfSeason() {
        Map<TierType, List<RankedClan>> finalStandings =
                collectFinalStandings();
        List<ClanTierMovementDto> movements = new ArrayList<>();
        Set<Clan> clansToUpdate = new LinkedHashSet<>();

        for (Map.Entry<TierType, List<RankedClan>> tierStanding
                : finalStandings.entrySet()) {
            processTierStanding(
                    tierStanding.getKey(),
                    tierStanding.getValue(),
                    movements,
                    clansToUpdate);
        }

        if (!clansToUpdate.isEmpty()) {
            clanRepository.saveAll(clansToUpdate);
        }

        return new ClanSeasonResultDto(LocalDateTime.now(), movements);
    }

    private Map<TierType, List<RankedClan>> collectFinalStandings() {
        Map<TierType, List<RankedClan>> finalStandings =
                new EnumMap<>(TierType.class);
        for (TierType tierType : TierType.values()) {
            finalStandings.put(
                    tierType,
                    clanRankingService.getRankedClans(tierType));
        }
        return finalStandings;
    }

    private void processTierStanding(
            TierType tierType,
            List<RankedClan> rankedClans,
            List<ClanTierMovementDto> movements,
            Set<Clan> clansToUpdate) {
        int promotionSlots = calculatePromotionSlots(
                tierType,
                rankedClans.size());
        int demotionSlots = calculateDemotionSlots(
                tierType,
                rankedClans.size());

        for (RankedClan rankedClan : rankedClans) {
            TierType newTier = determineNewTier(
                    tierType,
                    rankedClan.rank(),
                    rankedClans.size(),
                    promotionSlots,
                    demotionSlots);
            movements.add(toMovement(rankedClan, tierType, newTier));

            if (newTier != tierType) {
                rankedClan.clan().setTierType(newTier);
                clansToUpdate.add(rankedClan.clan());
            }
        }
    }

    private int calculatePromotionSlots(TierType tierType, int clanCount) {
        if (!tierType.hasHigherTier()
                || clanCount < MINIMUM_CLANS_FOR_MOVEMENT) {
            return 0;
        }
        return Math.min(PROMOTION_SLOTS, clanCount / 2);
    }

    private int calculateDemotionSlots(TierType tierType, int clanCount) {
        if (!tierType.hasLowerTier()
                || clanCount < MINIMUM_CLANS_FOR_MOVEMENT) {
            return 0;
        }
        return Math.min(DEMOTION_SLOTS, clanCount / 2);
    }

    private TierType determineNewTier(
            TierType tierType,
            int rank,
            int clanCount,
            int promotionSlots,
            int demotionSlots) {
        if (rank <= promotionSlots) {
            return tierType.getHigherTier();
        }
        if (rank > clanCount - demotionSlots) {
            return tierType.getLowerTier();
        }
        return tierType;
    }

    private ClanTierMovementDto toMovement(
            RankedClan rankedClan,
            TierType previousTier,
            TierType newTier) {
        Clan clan = rankedClan.clan();
        return new ClanTierMovementDto(
                clan.getId(),
                clan.getNama(),
                previousTier,
                newTier,
                rankedClan.rank(),
                rankedClan.score(),
                determineMovementType(previousTier, newTier));
    }

    private ClanTierMovementType determineMovementType(
            TierType previousTier,
            TierType newTier) {
        if (newTier.ordinal() > previousTier.ordinal()) {
            return ClanTierMovementType.PROMOTED;
        }
        if (newTier.ordinal() < previousTier.ordinal()) {
            return ClanTierMovementType.DEMOTED;
        }
        return ClanTierMovementType.STAYED;
    }
}
