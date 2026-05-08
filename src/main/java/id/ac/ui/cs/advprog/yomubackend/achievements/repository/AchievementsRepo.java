package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MilestoneType;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Repository untuk entitas Achievement. */
@Repository
public interface AchievementsRepo extends JpaRepository<Achievements, String> {

    /**
     * Temukan semua achievement dengan tipe milestone tertentu.
     * Digunakan oleh UserProgressService untuk mencocokkan event yang masuk.
     *
     * @param milestoneType tipe milestone
     * @return list achievement yang relevan
     */
    List<Achievements> findByMilestoneType(MilestoneType milestoneType);
}