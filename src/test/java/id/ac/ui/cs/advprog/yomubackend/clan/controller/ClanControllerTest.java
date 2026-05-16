package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.model.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:clan-controller-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ClanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClanRepository clanRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        clanRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @Transactional
    void simpanClanStoresClanAndRedirectsToClanPage() throws Exception {
        User user = new User();
        user.setUsername("ketua");
        user.setDisplayName("Ketua Clan");
        user.setPassword("password");
        user.setRole("PELAJAR");
        userRepository.save(user);

        mockMvc.perform(post("/clan/create-clan")
                        .with(user("ketua"))
                        .param("nama", "Yomu Readers"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clan"));

        assertThat(clanRepository.findAll())
                .singleElement()
                .satisfies(clan -> {
                    assertThat(clan.getNama()).isEqualTo("Yomu Readers");
                    assertThat(clan.getKetuaClan().getUsername()).isEqualTo("ketua");
                    assertThat(clan.getAnggota())
                            .extracting(User::getUsername)
                            .containsExactly("ketua");
                });
    }

    @Test
    @Transactional
    void deleteClanDeletesClanWhenLoggedInUserIsKetuaClan() throws Exception {
        User ketua = createUser("ketua");
        Clan clan = createClan("Yomu Readers", ketua);

        mockMvc.perform(post("/clan/delete-clan")
                        .with(user("ketua"))
                        .param("id", clan.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clan"));

        assertThat(clanRepository.findById(clan.getId())).isEmpty();
    }

    @Test
    @Transactional
    void deleteClanDoesNotDeleteClanWhenLoggedInUserIsNotKetuaClan() throws Exception {
        User ketua = createUser("ketua");
        createUser("anggota");
        Clan clan = createClan("Yomu Readers", ketua);

        mockMvc.perform(post("/clan/delete-clan")
                        .with(user("anggota"))
                        .param("id", clan.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clan"));

        assertThat(clanRepository.findById(clan.getId())).isPresent();
    }

    private User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(username);
        user.setPassword("password");
        user.setRole("PELAJAR");
        return userRepository.save(user);
    }

    private Clan createClan(String nama, User ketua) {
        Clan clan = new Clan();
        clan.setNama(nama);
        clan.jadikanKetuaSebagaiAnggotaAwal(ketua);
        return clanRepository.saveAndFlush(clan);
    }
}
