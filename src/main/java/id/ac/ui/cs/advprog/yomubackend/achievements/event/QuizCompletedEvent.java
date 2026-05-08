package id.ac.ui.cs.advprog.yomubackend.achievements.event;

import org.springframework.context.ApplicationEvent;

/**
 * Event yang dipublikasikan oleh Modul Bacaan setiap kali seorang pelajar
 * berhasil menyelesaikan kuis dari sebuah bacaan.
 *
 * <p>Modul Achievement mendengarkan event ini (via {@link AchievementProgressListener})
 * untuk memperbarui progress achievement dan daily mission tanpa perlu
 * coupling langsung ke Modul Bacaan.</p>
 *
 * <p>Desain: Spring {@link ApplicationEvent} dipilih sebagai mekanisme
 * komunikasi antar modul agar setiap modul tetap independen sesuai constraint
 * sistem — publisher tidak mengetahui siapa yang mendengarkan event-nya.</p>
 */
public class QuizCompletedEvent extends ApplicationEvent {

    /** Username pelajar yang menyelesaikan kuis. */
    private final String username;

    /** ID bacaan yang baru diselesaikan. */
    private final Long bacaanId;

    /** Kategori bacaan (contoh: "News", "Olahraga"). */
    private final String kategori;

    /** Skor kuis (0–100). */
    private final int score;

    /**
     * Konstruktor event.
     *
     * @param source    sumber event (biasanya controller atau service)
     * @param username  username pelajar
     * @param bacaanId  ID bacaan
     * @param kategori  kategori bacaan
     * @param score     skor kuis (0–100)
     */
    public QuizCompletedEvent(
            final Object source,
            final String username,
            final Long bacaanId,
            final String kategori,
            final int score) {
        super(source);
        this.username = username;
        this.bacaanId = bacaanId;
        this.kategori = kategori;
        this.score = score;
    }

    /** @return username pelajar */
    public String getUsername() {
        return username;
    }

    /** @return ID bacaan */
    public Long getBacaanId() {
        return bacaanId;
    }

    /** @return kategori bacaan */
    public String getKategori() {
        return kategori;
    }

    /** @return skor kuis 0–100 */
    public int getScore() {
        return score;
    }
}
