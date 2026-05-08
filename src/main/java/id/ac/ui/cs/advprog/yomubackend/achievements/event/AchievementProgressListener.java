package id.ac.ui.cs.advprog.yomubackend.achievements.event;

import id.ac.ui.cs.advprog.yomubackend.achievements.service.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Listener yang mendengarkan event dari modul lain dan mendelegasikan
 * pemrosesan progress ke {@link UserProgressService}.
 *
 * <p>Dijalankan secara {@link Async} agar tidak memblokir alur utama
 * Modul Bacaan saat menulis riwayat kuis.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AchievementProgressListener {

    /** Service yang memproses progress achievement dan daily mission. */
    private final UserProgressService userProgressService;

    /**
     * Tangani event penyelesaian kuis dari Modul Bacaan.
     *
     * @param event data penyelesaian kuis
     */
    @EventListener
    @Async
    public void onQuizCompleted(final QuizCompletedEvent event) {
        log.info("[Achievement] QuizCompletedEvent diterima: user={}, bacaan={}, score={}",
                event.getUsername(), event.getBacaanId(), event.getScore());
        try {
            userProgressService.handleQuizCompleted(
                    event.getUsername(),
                    event.getKategori(),
                    event.getScore());
        } catch (Exception ex) {
            log.error("[Achievement] Gagal memproses QuizCompletedEvent untuk user={}: {}",
                    event.getUsername(), ex.getMessage(), ex);
        }
    }
}
