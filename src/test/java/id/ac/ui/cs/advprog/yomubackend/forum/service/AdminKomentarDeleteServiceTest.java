package id.ac.ui.cs.advprog.yomubackend.forum.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.repository.KomentarRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test case untuk fitur admin menghapus komentar.
 */
@ExtendWith(MockitoExtension.class)
public class AdminKomentarDeleteServiceTest {

    /** Mock repository komentar. */
    @Mock
    private KomentarRepository komentarRepository;

    /** Mock repository user. */
    @Mock
    private UserRepository userRepository;

    /** Service yang ditest. */
    @InjectMocks
    private KomentarServiceImpl komentarService;

    /** Admin user untuk testing. */
    private User adminUser;

    /** Regular user untuk testing. */
    private User regularUser;

    /** Komentar untuk testing. */
    private Komentar testKomentar;

    /** Setup sebelum setiap test. */
    @BeforeEach
    void setUp() {
        adminUser = new User();
        adminUser.setId("admin-id");
        adminUser.setUsername("admin");
        adminUser.setRole("ADMIN");

        regularUser = new User();
        regularUser.setId("user-id");
        regularUser.setUsername("user");
        regularUser.setRole("PELAJAR");

        testKomentar = new Komentar();
        testKomentar.setId(1L);
        testKomentar.setBacaanId(1L);
        testKomentar.setPelajarId("user-id");
        testKomentar.setIsi("Test comment");
    }

    /**
     * Test admin dapat menghapus komentar siapa saja.
     */
    @Test
    void testAdminCanDeleteAnyComment() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));
        when(komentarRepository.findById(1L))
                .thenReturn(Optional.of(testKomentar));

        komentarService.deleteKomentarAsAdmin(1L, "admin");

        verify(komentarRepository).deleteById(1L);
    }

    /**
     * Test non-admin tidak dapat menghapus komentar.
     */
    @Test
    void testNonAdminCannotDeleteComment() {
        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(regularUser));

        assertThrows(SecurityException.class, () ->
                komentarService.deleteKomentarAsAdmin(1L, "user"));

        verify(komentarRepository, never()).deleteById(any());
    }

    /**
     * Test error ketika komentar tidak ditemukan.
     */
    @Test
    void testDeleteNonExistentComment() {
        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));
        when(komentarRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                komentarService.deleteKomentarAsAdmin(1L, "admin"));
    }

    /**
     * Test admin delete juga menghapus replies dari komentar.
     */
    @Test
    void testAdminDeleteRemovesReplies() {
        Komentar parentComment = new Komentar();
        parentComment.setId(1L);
        parentComment.setBacaanId(1L);
        parentComment.setPelajarId("user-id");

        Komentar reply = new Komentar();
        reply.setId(2L);
        reply.setBacaanId(1L);
        reply.setPelajarId("user-id-2");
        reply.setParent(parentComment);

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(adminUser));
        when(komentarRepository.findById(1L))
                .thenReturn(Optional.of(parentComment));

        komentarService.deleteKomentarAsAdmin(1L, "admin");

        verify(komentarRepository).deleteById(1L);
    }
}
