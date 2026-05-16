package id.ac.ui.cs.advprog.yomubackend.achievements.repository;

import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, UUID> {
}
