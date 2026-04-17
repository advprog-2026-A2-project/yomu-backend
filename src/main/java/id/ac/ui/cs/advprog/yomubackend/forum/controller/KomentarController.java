package id.ac.ui.cs.advprog.yomubackend.forum.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.model.KomentarRequest;
import id.ac.ui.cs.advprog.yomubackend.forum.model.ReaksiRequest;
import id.ac.ui.cs.advprog.yomubackend.forum.service.KomentarService;
import lombok.RequiredArgsConstructor;

/**
 * Controller REST untuk mengelola komentar pada forum diskusi.
 */
@RestController
@RequestMapping("/api/forum/komentar")
@RequiredArgsConstructor
public final class KomentarController {

    /** Service untuk operasi komentar. */
    private final KomentarService komentarService;

    /** Repository untuk akses data user. */
    private final UserRepository userRepository;

    /**
     * Mendapatkan user ID dari principal.
     *
     * @param principal data user yang sedang login
     * @return user ID
     * @throws SecurityException jika user belum login
     * @throws IllegalArgumentException jika data user tidak ditemukan
     */
    private String getUserId(final Principal principal) {
        if (principal == null) {
            throw new SecurityException(
                    "User belum login atau sesi telah habis.");
        }
        return userRepository.findByUsername(principal.getName())
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Data User tidak ditemukan di database."));
    }

    /**
     * Membuat komentar baru.
     *
     * @param request data komentar yang akan dibuat
     * @param principal data user yang sedang login
     * @return response dengan data komentar yang dibuat
     */
    @PostMapping
    public ResponseEntity<?> createKomentar(
            @RequestBody final KomentarRequest request,
            final Principal principal) {
        try {
            request.setPelajarId(getUserId(principal));
            Komentar saved = komentarService.createKomentar(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Gagal menyimpan: " + e.getMessage());
        }
    }

    /**
     * Mengupdate komentar yang sudah ada.
     *
     * @param id ID komentar yang akan diupdate
     * @param request data komentar yang diperbarui
     * @param principal data user yang sedang login
     * @return response dengan data komentar yang diperbarui
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateKomentar(
            @PathVariable final Long id,
            @RequestBody final KomentarRequest request,
            final Principal principal) {
        try {
            return ResponseEntity.ok(
                    komentarService.updateKomentar(id,
                            getUserId(principal), request.getIsi()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Gagal mengedit: " + e.getMessage());
        }
    }

    /**
     * Menghapus komentar.
     *
     * @param id ID komentar yang akan dihapus
     * @param principal data user yang sedang login
     * @return response kosong jika berhasil
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKomentar(
            @PathVariable final Long id,
            final Principal principal) {
        try {
            komentarService.deleteKomentar(id, getUserId(principal));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Gagal menghapus: " + e.getMessage());
        }
    }

    /**
     * Mengambil semua komentar utama untuk suatu bacaan.
     *
     * @param bacaanId ID bacaan
     * @return daftar komentar untuk bacaan tersebut
     */
    @GetMapping("/bacaan/{bacaanId}")
    public ResponseEntity<List<Komentar>> getKomentarByBacaanId(
            @PathVariable final Long bacaanId) {
        return ResponseEntity.ok(
                komentarService.getKomentarUtamaByBacaanId(bacaanId));
    }

    /**
     * Menambah atau menghapus reaksi pada komentar.
     *
     * @param id ID komentar
     * @param request data reaksi
     * @param principal data user yang sedang login
     * @return response kosong jika berhasil
     */
    @PostMapping("/{id}/reaksi")
    public ResponseEntity<?> toggleReaksi(
            @PathVariable final Long id,
            @RequestBody final ReaksiRequest request,
            final Principal principal) {
        try {
            komentarService.toggleReaksi(id, getUserId(principal),
                    request.getJenisReaksi());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Gagal memberikan reaksi: " + e.getMessage());
        }
    }
}

