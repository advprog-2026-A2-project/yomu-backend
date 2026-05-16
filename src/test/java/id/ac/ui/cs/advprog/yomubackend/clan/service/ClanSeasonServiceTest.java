package id.ac.ui.cs.advprog.yomubackend.clan.service;

import static org.assertj.core.api.Assertions.assertThat;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.achievements.repository.UserDailyMissionRepository;
import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanSeasonResultDto;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanScoreModifierType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.ClanTierMovementType;
import id.ac.ui.cs.advprog.yomubackend.clan.enums.TierType;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:clan-season-service-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class ClanSeasonServiceTest {

    @Autowired
    private ClanSeasonService clanSeasonService;

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
    void processEndOfSeasonPromotesTopClanAndDemotesBottomClanPerTier() {
        Bacaan quiz = createBacaan("Season Quiz");

        Clan bronzeTop = createScoredClan(
                "Bronze Top",
                TierType.BRONZE,
                "bronzeTop",
                quiz,
                90);
        Clan bronzeBottom = createScoredClan(
                "Bronze Bottom",
                TierType.BRONZE,
                "bronzeBottom",
                quiz,
                10);
        Clan silverTop = createScoredClan(
                "Silver Top",
                TierType.SILVER,
                "silverTop",
                quiz,
                90);
        Clan silverBottom = createScoredClan(
                "Silver Bottom",
                TierType.SILVER,
                "silverBottom",
                quiz,
                10);
        Clan goldTop = createScoredClan(
                "Gold Top",
                TierType.GOLD,
                "goldTop",
                quiz,
                90);
        Clan goldBottom = createScoredClan(
                "Gold Bottom",
                TierType.GOLD,
                "goldBottom",
                quiz,
                10);
        Clan diamondTop = createScoredClan(
                "Diamond Top",
                TierType.DIAMOND,
                "diamondTop",
                quiz,
                90);
        Clan diamondBottom = createScoredClan(
                "Diamond Bottom",
                TierType.DIAMOND,
                "diamondBottom",
                quiz,
                10);

        ClanSeasonResultDto result = clanSeasonService.processEndOfSeason();

        assertThat(result.getPromotedCount()).isEqualTo(3);
        assertThat(result.getDemotedCount()).isEqualTo(3);
        assertThat(result.getStayedCount()).isEqualTo(2);
        assertThat(result.getMovements())
                .extracting("movementType")
                .contains(ClanTierMovementType.PROMOTED,
                        ClanTierMovementType.DEMOTED,
                        ClanTierMovementType.STAYED);

        assertTier(bronzeTop, TierType.SILVER);
        assertTier(bronzeBottom, TierType.BRONZE);
        assertTier(silverTop, TierType.GOLD);
        assertTier(silverBottom, TierType.BRONZE);
        assertTier(goldTop, TierType.DIAMOND);
        assertTier(goldBottom, TierType.SILVER);
        assertTier(diamondTop, TierType.DIAMOND);
        assertTier(diamondBottom, TierType.GOLD);
    }

    @Test
    void processEndOfSeasonUsesFinalLeaderboardScoreWithModifiers() {
        Bacaan quiz = createBacaan("Modifier Quiz");
        Clan boostedClan = createScoredClan(
                "Boosted Clan",
                TierType.BRONZE,
                "boostedClan",
                quiz,
                40);
        boostedClan.aktifkanScoreModifier(ClanScoreModifierType.DOUBLE_XP);
        clanRepository.saveAndFlush(boostedClan);
        Clan steadyClan = createScoredClan(
                "Steady Clan",
                TierType.BRONZE,
                "steadyClan",
                quiz,
                60);

        ClanSeasonResultDto result = clanSeasonService.processEndOfSeason();

        assertThat(result.getMovements())
                .filteredOn(movement -> movement.getClanId()
                        .equals(boostedClan.getId()))
                .singleElement()
                .satisfies(movement -> {
                    assertThat(movement.getFinalRank()).isEqualTo(1);
                    assertThat(movement.getMovementType())
                            .isEqualTo(ClanTierMovementType.PROMOTED);
                });
        assertTier(boostedClan, TierType.SILVER);
        assertTier(steadyClan, TierType.BRONZE);
    }

    private Clan createScoredClan(
            String clanName,
            TierType tierType,
            String username,
            Bacaan quiz,
            int score) {
        User user = createUser(username);
        Clan clan = createClan(clanName, tierType, user);
        createHistory(username, quiz, score);
        return clan;
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

    private Bacaan createBacaan(String title) {
        Bacaan bacaan = new Bacaan();
        bacaan.setJudul(title);
        bacaan.setKonten("Konten " + title);
        Bacaan savedBacaan = bacaanRepository.saveAndFlush(bacaan);

        Pertanyaan pertanyaan = new Pertanyaan();
        pertanyaan.setSoal("Soal " + title);
        pertanyaan.setKunciJawaban("A");
        pertanyaan.setBacaan(savedBacaan);
        pertanyaanRepository.saveAndFlush(pertanyaan);

        return savedBacaan;
    }

    private void createHistory(String username, Bacaan bacaan, int nilai) {
        RiwayatKuis history = new RiwayatKuis();
        history.setUsername(username);
        history.setBacaan(bacaan);
        history.setNilai(nilai);
        riwayatKuisRepository.saveAndFlush(history);
    }

    private void assertTier(Clan clan, TierType expectedTier) {
        assertThat(clanRepository.findById(clan.getId()))
                .get()
                .extracting(Clan::getTierType)
                .isEqualTo(expectedTier);
    }
}
