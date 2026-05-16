package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import java.util.List;
import java.util.Objects;

abstract class AbstractQuizHistoryScoreStrategy implements ClanScoreStrategy {

    private final RiwayatKuisRepository riwayatKuisRepository;
    private final PertanyaanRepository pertanyaanRepository;

    AbstractQuizHistoryScoreStrategy(
            RiwayatKuisRepository riwayatKuisRepository,
            PertanyaanRepository pertanyaanRepository) {
        this.riwayatKuisRepository = riwayatKuisRepository;
        this.pertanyaanRepository = pertanyaanRepository;
    }

    protected List<RiwayatKuis> getHistories(Clan clan) {
        List<String> usernames = getMemberUsernames(clan);
        if (usernames.isEmpty()) {
            return List.of();
        }
        return riwayatKuisRepository.findByUsernameIn(usernames);
    }

    protected List<String> getMemberUsernames(Clan clan) {
        return clan.getAnggota().stream()
                .map(User::getUsername)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    protected int getQuizWeight(RiwayatKuis history) {
        if (history.getBacaan() == null
                || history.getBacaan().getId() == null) {
            return 1;
        }

        long questionCount = pertanyaanRepository
                .countByBacaanId(history.getBacaan().getId());
        return (int) Math.max(questionCount, 1);
    }
}
