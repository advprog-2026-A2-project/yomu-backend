package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanLeaderboardDto;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryDto;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanScoreModifierType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.service.ClanRankingService.RankedClan;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final ClanRepository clanRepository;
    private final ClanRankingService clanRankingService;

    public LeaderboardServiceImpl(
            ClanRepository clanRepository,
            ClanRankingService clanRankingService) {
        this.clanRepository = clanRepository;
        this.clanRankingService = clanRankingService;
    }

    @Override
    @Transactional(readOnly = true)
    public ClanLeaderboardDto getCurrentLeagueLeaderboard(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username wajib diisi");
        }

        Clan currentClan = getCurrentClan(username);
        TierType currentTier = currentClan.getTierType();

        List<RankedClan> rankedClans = clanRankingService
                .getRankedClans(currentTier);

        List<LeaderboardEntryDto> entries =
                rankedClans.stream()
                        .map(rankedClan -> toEntry(rankedClan, currentClan))
                        .toList();

        return new ClanLeaderboardDto(
                currentTier,
                currentClan.getId(),
                currentClan.getNama(),
                entries);
    }

    private Clan getCurrentClan(String username) {
        return clanRepository
                .findByAnggota_UsernameOrderByIdAsc(username)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Pelajar belum tergabung dalam clan"));
    }

    private LeaderboardEntryDto toEntry(RankedClan rankedClan, Clan currentClan) {
        Clan clan = rankedClan.clan();
        return new LeaderboardEntryDto(
                rankedClan.rank(),
                clan.getId(),
                clan.getNama(),
                clan.getTierType(),
                clan.getAnggota().size(),
                rankedClan.score(),
                Objects.equals(clan.getId(), currentClan.getId()),
                rankedClan.scoreMultiplier(),
                rankedClan.activeModifiers().stream()
                        .map(ClanScoreModifierType::getDisplayName)
                        .toList());
    }
}
