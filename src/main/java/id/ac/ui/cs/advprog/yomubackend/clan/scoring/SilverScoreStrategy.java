package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import org.springframework.stereotype.Component;

@Component
public class SilverScoreStrategy extends AbstractQuizHistoryScoreStrategy {

    public SilverScoreStrategy(
            RiwayatKuisRepository riwayatKuisRepository,
            PertanyaanRepository pertanyaanRepository) {
        super(riwayatKuisRepository, pertanyaanRepository);
    }

    @Override
    public TierType getTierType() {
        return TierType.SILVER;
    }

    @Override
    public double calculateScore(Clan clan) {
        return getHistories(clan).stream()
                .mapToDouble(RiwayatKuis::getNilai)
                .average()
                .orElse(0.0);
    }
}
