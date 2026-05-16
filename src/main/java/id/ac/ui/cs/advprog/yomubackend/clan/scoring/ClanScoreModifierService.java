package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanScoreModifierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import java.util.List;

public interface ClanScoreModifierService {

    List<ClanScoreModifierType> getActiveModifiers(Clan clan);

    double calculateFinalScore(
            double baseScore,
            List<ClanScoreModifierType> activeModifiers);
}
