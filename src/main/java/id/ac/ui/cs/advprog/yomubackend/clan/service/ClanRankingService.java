package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanScoreModifierType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.scoring.ClanScoreModifierService;
import id.ac.ui.cs.advprog.yomubackend.clan.scoring.ClanScoreStrategy;
import id.ac.ui.cs.advprog.yomubackend.clan.scoring.ClanScoreStrategyFactory;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;

@Service
public class ClanRankingService {

    private final ClanRepository clanRepository;
    private final ClanScoreStrategyFactory scoreStrategyFactory;
    private final ClanScoreModifierService scoreModifierService;

    public ClanRankingService(
            ClanRepository clanRepository,
            ClanScoreStrategyFactory scoreStrategyFactory,
            ClanScoreModifierService scoreModifierService) {
        this.clanRepository = clanRepository;
        this.scoreStrategyFactory = scoreStrategyFactory;
        this.scoreModifierService = scoreModifierService;
    }

    public List<RankedClan> getRankedClans(TierType tierType) {
        ClanScoreStrategy strategy = scoreStrategyFactory.getStrategy(tierType);

        List<ScoredClan> scoredClans = clanRepository
                .findByTierType(tierType)
                .stream()
                .map(clan -> scoreClan(clan, strategy))
                .sorted(Comparator
                        .comparingDouble(ScoredClan::score)
                        .reversed()
                        .thenComparing(scoredClan ->
                                scoredClan.clan().getNama(),
                                String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(scoredClan ->
                                scoredClan.clan().getId()))
                .toList();

        return IntStream.range(0, scoredClans.size())
                .mapToObj(index -> toRankedClan(
                        index + 1,
                        scoredClans.get(index)))
                .toList();
    }

    private ScoredClan scoreClan(Clan clan, ClanScoreStrategy strategy) {
        double baseScore = strategy.calculateScore(clan);
        List<ClanScoreModifierType> activeModifiers =
                scoreModifierService.getActiveModifiers(clan);
        double finalScore = scoreModifierService.calculateFinalScore(
                baseScore,
                activeModifiers);
        return new ScoredClan(
                clan,
                finalScore,
                calculateMultiplier(activeModifiers),
                activeModifiers);
    }

    private RankedClan toRankedClan(int rank, ScoredClan scoredClan) {
        return new RankedClan(
                scoredClan.clan(),
                rank,
                scoredClan.score(),
                scoredClan.scoreMultiplier(),
                scoredClan.activeModifiers());
    }

    private double calculateMultiplier(
            List<ClanScoreModifierType> activeModifiers) {
        return activeModifiers.stream()
                .mapToDouble(ClanScoreModifierType::getMultiplier)
                .reduce(1.0, (left, right) -> left * right);
    }

    public record RankedClan(
            Clan clan,
            int rank,
            double score,
            double scoreMultiplier,
            List<ClanScoreModifierType> activeModifiers) {

        public RankedClan {
            activeModifiers = List.copyOf(activeModifiers);
        }
    }

    private record ScoredClan(
            Clan clan,
            double score,
            double scoreMultiplier,
            List<ClanScoreModifierType> activeModifiers) {
    }
}
