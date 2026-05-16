package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GoldScoreStrategy extends AbstractQuizHistoryScoreStrategy {

    public GoldScoreStrategy(
            RiwayatKuisRepository riwayatKuisRepository,
            PertanyaanRepository pertanyaanRepository) {
        super(riwayatKuisRepository, pertanyaanRepository);
    }

    @Override
    public TierType getTierType() {
        return TierType.GOLD;
    }

    @Override
    public double calculateScore(Clan clan) {
        Map<String, Integer> scoreByMember = getHistories(clan).stream()
                .collect(Collectors.groupingBy(
                        RiwayatKuis::getUsername,
                        Collectors.summingInt(RiwayatKuis::getNilai)));

        if (scoreByMember.isEmpty()) {
            return 0.0;
        }

        return scoreByMember.values().stream()
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(0.0);
    }
}
