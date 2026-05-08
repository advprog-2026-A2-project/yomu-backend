package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.DailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserDailyMissionResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepo;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementasi {@link DailyMissionService}.
 */
@Service
@RequiredArgsConstructor
public class DailyMissionServiceImpl implements DailyMissionService {

    /** Repository untuk entitas DailyMission. */
    private final DailyMissionRepo dailyMissionRepo;

    /** Repository untuk progress user terhadap misi harian. */
    private final UserDailyMissionRepo userDailyMissionRepo;

    // ----------------------------------------------------------------
    // Admin use cases
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public DailyMissionResponse createMission(final DailyMissionRequest request) {
        final DailyMission mission = DailyMission.builder()
                .name(request.getName())
                .description(request.getDescription())
                .requirementType(request.getRequirementType())
                .requirementTargetValue(request.getRequirementTargetValue())
                .requirementCategory(request.getRequirementCategory())
                .rewardXp(request.getRewardXp())
                .isActive(request.isActive())
                .build();
        return new DailyMissionResponse(dailyMissionRepo.save(mission));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyMissionResponse> getAllMissions() {
        return dailyMissionRepo.findAll().stream()
                .map(DailyMissionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DailyMissionResponse> getActiveMissions() {
        return dailyMissionRepo.findByIsActiveTrue().stream()
                .map(DailyMissionResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DailyMissionResponse updateMission(
            final UUID missionId, final DailyMissionRequest request) {
        final DailyMission mission = findOrThrow(missionId);
        mission.updateDetails(
                request.getName(),
                request.getDescription(),
                request.getRewardXp(),
                request.getRequirementType(),
                request.getRequirementTargetValue(),
                request.getRequirementCategory());
        return new DailyMissionResponse(dailyMissionRepo.save(mission));
    }

    @Override
    @Transactional
    public void deleteMission(final UUID missionId) {
        if (!dailyMissionRepo.existsById(missionId)) {
            throw new NoSuchElementException("DailyMission tidak ditemukan: " + missionId);
        }
        dailyMissionRepo.deleteById(missionId);
    }

    @Override
    @Transactional
    public DailyMissionResponse activateMission(final UUID missionId) {
        final DailyMission mission = findOrThrow(missionId);
        mission.activate();
        return new DailyMissionResponse(dailyMissionRepo.save(mission));
    }

    @Override
    @Transactional
    public DailyMissionResponse deactivateMission(final UUID missionId) {
        final DailyMission mission = findOrThrow(missionId);
        mission.deactivate();
        return new DailyMissionResponse(dailyMissionRepo.save(mission));
    }

    // ----------------------------------------------------------------
    // Pelajar use cases
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public List<UserDailyMissionResponse> getMyTodayMissions(final UUID userId) {
        final LocalDate today = LocalDate.now();
        final List<DailyMission> activeMissions = dailyMissionRepo.findByIsActiveTrue();
        final List<UserDailyMissionResponse> result = new ArrayList<>();

        for (DailyMission mission : activeMissions) {
            // Cari record yang sudah ada, atau buat baru (lazy initialization)
            UserDailyMission udm = userDailyMissionRepo
                    .findByUserIdAndMissionAndDate(userId, mission, today)
                    .orElseGet(() -> {
                        UserDailyMission newUdm = UserDailyMission.builder()
                                .userId(userId)
                                .mission(mission)
                                .date(today)
                                .currentProgress(0)
                                .isCompleted(false)
                                .rewardClaimed(false)
                                .build();
                        return userDailyMissionRepo.save(newUdm);
                    });
            result.add(new UserDailyMissionResponse(udm));
        }
        return result;
    }

    @Override
    @Transactional
    public UserDailyMissionResponse claimReward(
            final UUID userId, final UUID userMissionId) {
        final UserDailyMission udm = userDailyMissionRepo
                .findById(userMissionId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserDailyMission tidak ditemukan: " + userMissionId));

        if (!udm.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Misi ini bukan milik user.");
        }

        udm.claimReward(); // throws jika belum selesai atau sudah diklaim
        return new UserDailyMissionResponse(userDailyMissionRepo.save(udm));
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------

    private DailyMission findOrThrow(final UUID missionId) {
        return dailyMissionRepo.findById(missionId)
                .orElseThrow(() -> new NoSuchElementException(
                        "DailyMission tidak ditemukan: " + missionId));
    }
}
