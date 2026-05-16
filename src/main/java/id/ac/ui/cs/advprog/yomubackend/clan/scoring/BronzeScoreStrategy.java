package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import org.springframework.stereotype.Component;

@Component
public class BronzeScoreStrategy extends AbstractQuizHistoryScoreStrategy {

    public BronzeScoreStrategy(
            RiwayatKuisRepository riwayatKuisRepository,
            PertanyaanRepository pertanyaanRepository) {
        super(riwayatKuisRepository, pertanyaanRepository);
    }

    @Override
    public TierType getTierType() {
        return TierType.BRONZE;
    }

    @Override
    public double calculateScore(Clan clan) {
        return getHistories(clan).stream()
                .mapToDouble(RiwayatKuis::getNilai)
                .sum();
    }
}
