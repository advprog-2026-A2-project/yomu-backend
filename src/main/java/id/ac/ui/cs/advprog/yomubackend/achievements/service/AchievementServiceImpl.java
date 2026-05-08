package id.ac.ui.cs.advprog.yomubackend.achievements.service;

import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementRequest;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.AchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.dto.UserAchievementResponse;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.Achievements;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserAchievements;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.AchievementsRepo;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserAchievementsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Implementasi {@link AchievementService}.
 */
@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    /** Repository untuk entitas Achievement. */
    private final AchievementsRepo achievementsRepo;

    /** Repository untuk progress user. */
    private final UserAchievementsRepo userAchievementsRepo;

    // ----------------------------------------------------------------
    // Admin use cases
    // ----------------------------------------------------------------

    @Override
    @Transactional
    public AchievementResponse createAchievement(final AchievementRequest request) {
        final Achievements achievement = new Achievements();
        achievement.setAchievementName(request.getAchievementName());
        achievement.setAchievementDescription(request.getAchievementDescription());
        achievement.setMilestoneType(request.getMilestoneType());
        achievement.setMilestoneTargetValue(request.getMilestoneTargetValue());
        achievement.setIconUrl(request.getIconUrl());
        return new AchievementResponse(achievementsRepo.save(achievement));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementResponse> getAllAchievements() {
        return achievementsRepo.findAll().stream()
                .map(AchievementResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AchievementResponse getAchievementById(final String achievementId) {
        return new AchievementResponse(findOrThrow(achievementId));
    }

    @Override
    @Transactional
    public AchievementResponse updateAchievement(
            final String achievementId, final AchievementRequest request) {
        final Achievements achievement = findOrThrow(achievementId);
        achievement.updateDetails(
                request.getAchievementName(),
                request.getAchievementDescription(),
                request.getIconUrl(),
                request.getMilestoneType(),
                request.getMilestoneTargetValue());
        return new AchievementResponse(achievementsRepo.save(achievement));
    }

    @Override
    @Transactional
    public void deleteAchievement(final String achievementId) {
        if (!achievementsRepo.existsById(achievementId)) {
            throw new NoSuchElementException("Achievement tidak ditemukan: " + achievementId);
        }
        achievementsRepo.deleteById(achievementId);
    }

    // ----------------------------------------------------------------
    // Pelajar use cases
    // ----------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> getMyAchievements(final String userId) {
        return userAchievementsRepo.findByUserId(userId).stream()
                .map(UserAchievementResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAchievementResponse> getDisplayedAchievements(final String userId) {
        return userAchievementsRepo
                .findByUserIdAndIsCompletedAndIsDisplayed(userId, true, true)
                .stream()
                .map(UserAchievementResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserAchievementResponse toggleDisplay(
            final String userId, final String userAchievementId) {
        final UserAchievements ua = userAchievementsRepo
                .findById(userAchievementId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserAchievement tidak ditemukan: " + userAchievementId));

        if (!ua.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Achievement ini bukan milik user.");
        }

        ua.toggleDisplay(); // throws IllegalStateException jika belum selesai
        return new UserAchievementResponse(userAchievementsRepo.save(ua));
    }

    // ----------------------------------------------------------------
    // Helper
    // ----------------------------------------------------------------

    private Achievements findOrThrow(final String achievementId) {
        return achievementsRepo.findById(achievementId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Achievement tidak ditemukan: " + achievementId));
    }
}
