package id.ac.ui.cs.advprog.yomubackend.clan.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:clan-season-admin-controller-test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ClanSeasonAdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void processEndOfSeasonReturnsResultForAdmin() throws Exception {
        mockMvc.perform(post("/api/admin/clan/seasons/end")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promotedCount").value(0))
                .andExpect(jsonPath("$.demotedCount").value(0))
                .andExpect(jsonPath("$.stayedCount").value(0));
    }

    @Test
    void processEndOfSeasonRejectsNonAdminUser() throws Exception {
        mockMvc.perform(post("/api/admin/clan/seasons/end")
                        .with(user("pelajar").roles("PELAJAR")))
                .andExpect(status().isForbidden());
    }
}
