package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
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
}
