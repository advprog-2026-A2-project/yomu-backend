package id.ac.ui.cs.advprog.yomubackend.clan.scoring;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanScoreModifierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ClanScoreModifierServiceImpl implements ClanScoreModifierService {

    private static final double PRODUCTIVITY_COMPLETION_THRESHOLD = 0.5;
    private static final double LOW_ACCURACY_THRESHOLD = 50.0;

    private final RiwayatKuisRepository riwayatKuisRepository;
    private final UserDailyMissionRepository userDailyMissionRepository;

    public ClanScoreModifierServiceImpl(
            RiwayatKuisRepository riwayatKuisRepository,
            UserDailyMissionRepository userDailyMissionRepository) {
        this.riwayatKuisRepository = riwayatKuisRepository;
        this.userDailyMissionRepository = userDailyMissionRepository;
    }

    @Override
    public List<ClanScoreModifierType> getActiveModifiers(Clan clan) {
        List<ClanScoreModifierType> activeModifiers = new ArrayList<>();
        if (clan.getActiveScoreModifiers() != null) {
            clan.getActiveScoreModifiers().stream()
                    .filter(Objects::nonNull)
                    .forEach(activeModifiers::add);
        }

        if (isProductivityBuffActive(clan)) {
            activeModifiers.add(ClanScoreModifierType.PRODUCTIVITY_BUFF);
        }
        if (isLowAccuracyPenaltyActive(clan)) {
            activeModifiers.add(ClanScoreModifierType.LOW_ACCURACY_PENALTY);
        }
        return List.copyOf(activeModifiers);
    }

    @Override
    public double calculateFinalScore(
            double baseScore,
            List<ClanScoreModifierType> activeModifiers) {
        double totalMultiplier = activeModifiers.stream()
                .mapToDouble(ClanScoreModifierType::getMultiplier)
                .reduce(1.0, (left, right) -> left * right);
        return baseScore * totalMultiplier;
    }

    private boolean isProductivityBuffActive(Clan clan) {
        List<UUID> memberIds = getMemberIds(clan);
        if (memberIds.isEmpty()) {
            return false;
        }

        long completedMemberCount = userDailyMissionRepository
                .countMembersWithCompletedMission(memberIds, LocalDate.now());
        double completionRatio = completedMemberCount / (double) memberIds.size();
        return completionRatio >= PRODUCTIVITY_COMPLETION_THRESHOLD;
    }

    private boolean isLowAccuracyPenaltyActive(Clan clan) {
        List<String> usernames = getMemberUsernames(clan);
        if (usernames.isEmpty()) {
            return false;
        }

        List<RiwayatKuis> histories = riwayatKuisRepository.findByUsernameIn(usernames);
        if (histories.isEmpty()) {
            return false;
        }

        double averageAccuracy = histories.stream()
                .mapToInt(RiwayatKuis::getNilai)
                .average()
                .orElse(LOW_ACCURACY_THRESHOLD);
        return averageAccuracy < LOW_ACCURACY_THRESHOLD;
    }

    private List<String> getMemberUsernames(Clan clan) {
        return clan.getAnggota().stream()
                .map(User::getUsername)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<UUID> getMemberIds(Clan clan) {
        return clan.getAnggota().stream()
                .map(User::getId)
                .filter(Objects::nonNull)
                .map(this::toUuid)
                .flatMap(Optional::stream)
                .distinct()
                .toList();
    }

    private Optional<UUID> toUuid(String userId) {
        try {
            return Optional.of(UUID.fromString(userId));
        } catch (IllegalArgumentException exception) {
            return Optional.empty();
        }
    }
}
