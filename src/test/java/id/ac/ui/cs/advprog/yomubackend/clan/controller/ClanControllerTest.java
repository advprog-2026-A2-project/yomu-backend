package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import id.ac.ui.cs.advprog.yomubackend.clan.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ClanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClanRepository clanRepository;

    @BeforeEach
    void setUp() {
        clanRepository.deleteAll();
    }

    @Test
    void simpanClanStoresClanAndRedirectsToClanPage() throws Exception {
        mockMvc.perform(post("/clan/create-clan")
                        .param("nama", "Yomu Readers"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clan"));

        assertThat(clanRepository.findAll())
                .singleElement()
                .satisfies(clan -> assertThat(clan.getNama()).isEqualTo("Yomu Readers"));
    }
}
