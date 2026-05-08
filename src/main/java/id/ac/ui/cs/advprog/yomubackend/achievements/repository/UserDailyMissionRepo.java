package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Repository untuk entitas UserDailyMission. */
@Repository
public interface UserDailyMissionRepo extends JpaRepository<UserDailyMission, UUID> {

    /**
     * Ambil semua misi harian user untuk tanggal tertentu.
     *
     * @param userId ID user
     * @param date   tanggal misi
     * @return list progress misi user hari ini
     */
    List<UserDailyMission> findByUserIdAndDate(UUID userId, LocalDate date);

    /**
     * Cari record progress spesifik (userId + misionId + tanggal).
     * Digunakan untuk upsert agar tidak terjadi duplikasi.
     *
     * @param userId  ID user
     * @param mission misi terkait
     * @param date    tanggal misi
     * @return Optional record
     */
    Optional<UserDailyMission> findByUserIdAndMissionAndDate(
            UUID userId, DailyMission mission, LocalDate date);
}
