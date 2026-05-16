package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DiamondScoreStrategy extends AbstractQuizHistoryScoreStrategy {

    public DiamondScoreStrategy(
            RiwayatKuisRepository riwayatKuisRepository,
            PertanyaanRepository pertanyaanRepository) {
        super(riwayatKuisRepository, pertanyaanRepository);
    }

    @Override
    public TierType getTierType() {
        return TierType.DIAMOND;
    }

    @Override
    public double calculateScore(Clan clan) {
        List<RiwayatKuis> histories = getHistories(clan);
        int totalWeight = histories.stream()
                .mapToInt(this::getQuizWeight)
                .sum();

        if (totalWeight == 0) {
            return 0.0;
        }

        double weightedScore = histories.stream()
                .mapToDouble(history -> history.getNilai()
                        * getQuizWeight(history))
                .sum();
        return weightedScore / totalWeight;
    }
}
