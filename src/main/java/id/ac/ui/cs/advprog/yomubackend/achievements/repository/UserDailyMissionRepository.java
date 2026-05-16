package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDailyMissionRepository
        extends JpaRepository<UserDailyMission, UUID> {

    @Query("""
            SELECT COUNT(DISTINCT missionProgress.userId)
            FROM UserDailyMission missionProgress
            WHERE missionProgress.userId IN :userIds
            AND missionProgress.date = :date
            AND missionProgress.isCompleted = true
            """)
    long countMembersWithCompletedMission(
            @Param("userIds") Collection<UUID> userIds,
            @Param("date") LocalDate date);
}
