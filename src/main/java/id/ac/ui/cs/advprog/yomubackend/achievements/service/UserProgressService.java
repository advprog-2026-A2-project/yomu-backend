package id.ac.ui.cs.advprog.yomubackend.achievements.service;

/**
 * Kontrak service yang memproses progress achievement dan daily mission
 * berdasarkan aktivitas pelajar.
 *
 * <p>Service ini adalah titik masuk utama dari event listener — ia tidak
 * bergantung langsung pada Modul Bacaan, melainkan menerima data melalui
 * parameter yang diekstrak dari event.</p>
 */
public interface UserProgressService {

    /**
     * Proses perubahan progress setelah pelajar menyelesaikan kuis.
     *
     * <p>Dipanggil oleh {@link id.ac.ui.cs.advprog.yomubackend.achievements.event.AchievementProgressListener}
     * saat menerima {@link id.ac.ui.cs.advprog.yomubackend.achievements.event.QuizCompletedEvent}.</p>
     *
     * <p>Operasi yang dilakukan:
     * <ol>
     *   <li>Perbarui progress achievement bertipe {@code COMPLETE_READINGS}</li>
     *   <li>Perbarui progress achievement bertipe {@code QUIZ_ACCURACY}</li>
     *   <li>Perbarui progress daily mission sesuai tipe syaratnya</li>
     * </ol>
     * </p>
     *
     * @param username  username pelajar yang menyelesaikan kuis
     * @param kategori  kategori bacaan yang baru diselesaikan
     * @param score     skor kuis (0–100)
     */
    void handleQuizCompleted(String username, String kategori, int score);

    /**
     * Proses perubahan progress setelah pelajar bergabung dengan Clan.
     * Memperbarui achievement bertipe {@code JOIN_CLAN}.
     *
     * @param userId ID user
     */
    void handleJoinClan(String userId);

    /**
     * Proses perubahan progress setelah Clan pelajar dipromosikan ke tier lebih tinggi.
     * Memperbarui achievement bertipe {@code CLAN_PROMOTION}.
     *
     * @param userId ID user
     */
    void handleClanPromotion(String userId);
}
