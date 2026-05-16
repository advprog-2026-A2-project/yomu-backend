package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ClanScoreStrategyFactory {

    private final Map<TierType, ClanScoreStrategy> strategies;

    public ClanScoreStrategyFactory(List<ClanScoreStrategy> strategyList) {
        Map<TierType, ClanScoreStrategy> strategyMap =
                new EnumMap<>(TierType.class);
        strategyList.forEach(strategy ->
                strategyMap.put(strategy.getTierType(), strategy));
        this.strategies = Collections.unmodifiableMap(strategyMap);
    }

    public ClanScoreStrategy getStrategy(TierType tierType) {
        ClanScoreStrategy strategy = strategies.get(tierType);
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Strategy tidak ditemukan untuk tier " + tierType);
        }
        return strategy;
    }
}
