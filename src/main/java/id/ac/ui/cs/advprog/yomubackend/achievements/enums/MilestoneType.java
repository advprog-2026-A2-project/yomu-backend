package id.ac.ui.cs.advprog.yomubackend.achievements.enums;

/**
 * Tipe milestone yang menentukan kondisi penyelesaian Achievement.
 */
public enum MilestoneType {
    COMPLETE_READINGS,          // Menyelesaikan N bacaan
    COMPLETE_DAILY_MISSIONS,    // Menyelesaikan N daily mission
    QUIZ_ACCURACY,              // Mencapai akurasi kuis >= N%
    JOIN_CLAN,                  // Bergabung dengan Clan
    CLAN_PROMOTION,             // Clan dinaikkan tier
    STREAK_DAYS                 // Aktif N hari berturut-turut
}
