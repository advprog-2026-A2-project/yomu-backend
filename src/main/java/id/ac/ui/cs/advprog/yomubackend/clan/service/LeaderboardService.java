package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanLeaderboardDto;

public interface LeaderboardService {

    ClanLeaderboardDto getCurrentLeagueLeaderboard(String username);
}
