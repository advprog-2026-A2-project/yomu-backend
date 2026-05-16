package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanLeaderboardDto;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.LeaderboardEntryDto;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.scoring.ClanScoreStrategy;
import id.ac.ui.cs.advprog.yomubackend.clan.scoring.ClanScoreStrategyFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    private final ClanRepository clanRepository;
    private final ClanScoreStrategyFactory scoreStrategyFactory;

    public LeaderboardServiceImpl(
            ClanRepository clanRepository,
            ClanScoreStrategyFactory scoreStrategyFactory) {
        this.clanRepository = clanRepository;
        this.scoreStrategyFactory = scoreStrategyFactory;
    }

    @Override
    @Transactional(readOnly = true)
    public ClanLeaderboardDto getCurrentLeagueLeaderboard(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username wajib diisi");
        }

        Clan currentClan = getCurrentClan(username);
        TierType currentTier = currentClan.getTierType();
        ClanScoreStrategy strategy =
                scoreStrategyFactory.getStrategy(currentTier);

        List<ScoredClan> scoredClans = clanRepository
                .findByTierType(currentTier)
                .stream()
                .map(clan -> new ScoredClan(
                        clan,
                        strategy.calculateScore(clan)))
                .sorted(Comparator
                        .comparingDouble(ScoredClan::score)
                        .reversed()
                        .thenComparing(scoredClan ->
                                scoredClan.clan().getNama(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(scoredClan ->
                                scoredClan.clan().getId()))
                .toList();

        List<LeaderboardEntryDto> entries =
                IntStream.range(0, scoredClans.size())
                        .mapToObj(index -> toEntry(
                                index,
                                scoredClans.get(index),
                                currentClan))
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

    private LeaderboardEntryDto toEntry(
            int index,
            ScoredClan scoredClan,
            Clan currentClan) {
        Clan clan = scoredClan.clan();
        return new LeaderboardEntryDto(
                index + 1,
                clan.getId(),
                clan.getNama(),
                clan.getTierType(),
                clan.getAnggota().size(),
                scoredClan.score(),
                Objects.equals(clan.getId(), currentClan.getId()));
    }

    private record ScoredClan(Clan clan, double score) {
    }
}
