package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/** Repository untuk entitas DailyMission. */
@Repository
public interface DailyMissionRepo extends JpaRepository<DailyMission, UUID> {

    /**
     * Ambil semua misi yang sedang aktif.
     * Dipanggil setiap hari saat rotasi misi atau saat user membuka halaman misi.
     *
     * @return list misi aktif
     */
    List<DailyMission> findByIsActiveTrue();
}

