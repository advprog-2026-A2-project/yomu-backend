package id.ac.ui.cs.advprog.yomubackend.clan.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.enums.MissionRequirementType;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.DailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.model.UserDailyMission;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanLeaderboardDto;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanScoreModifierType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:leaderboard-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class LeaderboardServiceTest {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private ClanRepository clanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BacaanRepository bacaanRepository;

    @Autowired
    private PertanyaanRepository pertanyaanRepository;

    @Autowired
    private RiwayatKuisRepository riwayatKuisRepository;

    @Autowired
    private DailyMissionRepository dailyMissionRepository;

    @Autowired
    private UserDailyMissionRepository userDailyMissionRepository;

    @BeforeEach
    void setUp() {
        userDailyMissionRepository.deleteAll();
        dailyMissionRepository.deleteAll();
        riwayatKuisRepository.deleteAll();
        pertanyaanRepository.deleteAll();
        bacaanRepository.deleteAll();
        clanRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void bronzeLeaderboardUsesTotalScoreAndSameTierOnly() {
        Bacaan shortQuiz = createBacaan("Short Quiz", 1);
        Bacaan longQuiz = createBacaan("Long Quiz", 2);

        User alphaUser = createUser("alpha");
        User betaUser = createUser("beta");
        User diamondUser = createUser("diamond");

        createClan("Alpha Readers", TierType.BRONZE, alphaUser);
        createClan("Beta Readers", TierType.BRONZE, betaUser);
        createClan("Diamond Readers", TierType.DIAMOND, diamondUser);

        createHistory("alpha", shortQuiz, 70);
        createHistory("alpha", longQuiz, 70);
        createHistory("beta", shortQuiz, 90);
        createHistory("beta", longQuiz, 70);
        createHistory("diamond", shortQuiz, 100);
        createHistory("diamond", longQuiz, 100);

        ClanLeaderboardDto leaderboard = leaderboardService
                .getCurrentLeagueLeaderboard("alpha");

        assertThat(leaderboard.getTierType()).isEqualTo(TierType.BRONZE);
        assertThat(leaderboard.getEntries())
                .extracting("clanName")
                .containsExactly("Beta Readers", "Alpha Readers");
        assertThat(leaderboard.getEntries())
                .extracting("score")
                .containsExactly(160.0, 140.0);
        assertThat(leaderboard.getEntries())
                .extracting("clanName")
                .doesNotContain("Diamond Readers");
        assertThat(leaderboard.getEntries().get(1).isCurrentUserClan())
                .isTrue();
    }

    @Test
    void diamondLeaderboardUsesQuestionCountWeightedAverage() {
        Bacaan oneQuestionQuiz = createBacaan("One Question Quiz", 1);
        Bacaan threeQuestionQuiz = createBacaan("Three Question Quiz", 3);

        User alphaUser = createUser("diamondAlpha");
        User betaUser = createUser("diamondBeta");

        createClan("Diamond Alpha", TierType.DIAMOND, alphaUser);
        createClan("Diamond Beta", TierType.DIAMOND, betaUser);

        createHistory("diamondAlpha", oneQuestionQuiz, 100);
        createHistory("diamondAlpha", threeQuestionQuiz, 0);
        createHistory("diamondBeta", oneQuestionQuiz, 50);
        createHistory("diamondBeta", threeQuestionQuiz, 50);

        ClanLeaderboardDto leaderboard = leaderboardService
                .getCurrentLeagueLeaderboard("diamondAlpha");

        assertThat(leaderboard.getEntries())
                .extracting("clanName")
                .containsExactly("Diamond Beta", "Diamond Alpha");
        assertThat(leaderboard.getEntries().get(0).getScore())
                .isCloseTo(50.0, within(0.001));
        assertThat(leaderboard.getEntries().get(1).getScore())
                .isCloseTo(25.0, within(0.001));
    }

    @Test
    void leaderboardAppliesProductivityBuffWhenHalfMembersCompleteDailyMission() {
        Bacaan quiz = createBacaan("Productive Quiz", 1);
        DailyMission mission = createDailyMission();

        User alpha = createUser("productiveAlpha");
        User beta = createUser("productiveBeta");
        Clan clan = createClan("Productive Readers", TierType.BRONZE, alpha);
        clan.tambahAnggota(beta);
        clanRepository.saveAndFlush(clan);

        createHistory("productiveAlpha", quiz, 50);
        createHistory("productiveBeta", quiz, 50);
        completeDailyMission(alpha, mission);

        ClanLeaderboardDto leaderboard = leaderboardService
                .getCurrentLeagueLeaderboard("productiveAlpha");

        assertThat(leaderboard.getEntries().getFirst().getScore())
                .isCloseTo(120.0, within(0.001));
        assertThat(leaderboard.getEntries().getFirst().getScoreMultiplier())
                .isCloseTo(1.2, within(0.001));
        assertThat(leaderboard.getEntries().getFirst().getActiveScoreModifiers())
                .containsExactly("Productivity Buff");
    }

    @Test
    void leaderboardAppliesLowAccuracyPenaltyWhenAverageAccuracyBelowFifty() {
        Bacaan quiz = createBacaan("Low Accuracy Quiz", 1);

        User alpha = createUser("lowAccuracyAlpha");
        createClan("Careful Readers", TierType.BRONZE, alpha);
        createHistory("lowAccuracyAlpha", quiz, 40);

        ClanLeaderboardDto leaderboard = leaderboardService
                .getCurrentLeagueLeaderboard("lowAccuracyAlpha");

        assertThat(leaderboard.getEntries().getFirst().getScore())
                .isCloseTo(32.0, within(0.001));
        assertThat(leaderboard.getEntries().getFirst().getScoreMultiplier())
                .isCloseTo(0.8, within(0.001));
        assertThat(leaderboard.getEntries().getFirst().getActiveScoreModifiers())
                .containsExactly("Low Accuracy Penalty");
    }

    @Test
    void scoreModifiersStackMultiplicativelyWithActiveClanItems() {
        Bacaan quiz = createBacaan("Stacking Quiz", 1);
        DailyMission mission = createDailyMission();

        User alpha = createUser("stackAlpha");
        User beta = createUser("stackBeta");
        Clan clan = createClan("Stacked Readers", TierType.BRONZE, alpha);
        clan.tambahAnggota(beta);
        clan.aktifkanScoreModifier(ClanScoreModifierType.DOUBLE_XP);
        clan.aktifkanScoreModifier(ClanScoreModifierType.DOUBLE_XP);
        clanRepository.saveAndFlush(clan);

        createHistory("stackAlpha", quiz, 40);
        createHistory("stackBeta", quiz, 40);
        completeDailyMission(alpha, mission);

        ClanLeaderboardDto leaderboard = leaderboardService
                .getCurrentLeagueLeaderboard("stackAlpha");

        assertThat(leaderboard.getEntries().getFirst().getScore())
                .isCloseTo(307.2, within(0.001));
        assertThat(leaderboard.getEntries().getFirst().getScoreMultiplier())
                .isCloseTo(3.84, within(0.001));
        assertThat(leaderboard.getEntries().getFirst().getActiveScoreModifiers())
                .containsExactly(
                        "Double XP",
                        "Double XP",
                        "Productivity Buff",
                        "Low Accuracy Penalty");
    }

    @Test
    void leaderboardThrowsWhenUserDoesNotHaveClan() {
        createUser("lonely");

        assertThatThrownBy(() -> leaderboardService
                .getCurrentLeagueLeaderboard("lonely"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("belum tergabung dalam clan");
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(username);
        user.setPassword("password");
        user.setRole("PELAJAR");
        return userRepository.saveAndFlush(user);
    }

    private Clan createClan(String name, TierType tierType, User ketua) {
        Clan clan = new Clan();
        clan.setNama(name);
        clan.setTierType(tierType);
        clan.jadikanKetuaSebagaiAnggotaAwal(ketua);
        return clanRepository.saveAndFlush(clan);
    }

    private Bacaan createBacaan(String title, int questionCount) {
        Bacaan bacaan = new Bacaan();
        bacaan.setJudul(title);
        bacaan.setKonten("Konten " + title);
        Bacaan savedBacaan = bacaanRepository.saveAndFlush(bacaan);

        for (int index = 0; index < questionCount; index++) {
            Pertanyaan pertanyaan = new Pertanyaan();
            pertanyaan.setSoal("Soal " + index);
            pertanyaan.setKunciJawaban("A");
            pertanyaan.setBacaan(savedBacaan);
            pertanyaanRepository.save(pertanyaan);
        }
        pertanyaanRepository.flush();
        return savedBacaan;
    }

    private void createHistory(String username, Bacaan bacaan, int nilai) {
        RiwayatKuis history = new RiwayatKuis();
        history.setUsername(username);
        history.setBacaan(bacaan);
        history.setNilai(nilai);
        riwayatKuisRepository.saveAndFlush(history);
    }

    private DailyMission createDailyMission() {
        DailyMission mission = DailyMission.builder()
                .name("Daily Reading")
                .description("Selesaikan bacaan harian")
                .requirementType(MissionRequirementType.COMPLETE_N_READINGS)
                .requirementTargetValue(1)
                .rewardXp(10)
                .isActive(true)
                .build();
        return dailyMissionRepository.saveAndFlush(mission);
    }

    private void completeDailyMission(User user, DailyMission mission) {
        UserDailyMission progress = UserDailyMission.builder()
                .userId(UUID.fromString(user.getId()))
                .mission(mission)
                .date(LocalDate.now())
                .currentProgress(1)
                .isCompleted(true)
                .completedAt(LocalDateTime.now())
                .rewardClaimed(false)
                .build();
        userDailyMissionRepository.saveAndFlush(progress);
    }
}
