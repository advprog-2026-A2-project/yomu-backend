package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;

public interface ClanScoreStrategy {

    TierType getTierType();

    double calculateScore(Clan clan);
}
