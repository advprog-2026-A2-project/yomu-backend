package id.ac.ui.cs.advprog.yomubackend.forum.controller;

import id.ac.ui.cs.advprog.yomubackend.auth.model.User;
import id.ac.ui.cs.advprog.yomubackend.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.model.KomentarRequest;
import id.ac.ui.cs.advprog.yomubackend.forum.model.ReaksiRequest;
import id.ac.ui.cs.advprog.yomubackend.forum.service.KomentarService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forum/komentar")
@RequiredArgsConstructor
public class KomentarController {

    private final KomentarService komentarService;
    private final UserRepository userRepository;

    private String getUserId(Principal principal) {
        if (principal == null) throw new SecurityException("User belum login atau sesi telah habis.");
        return userRepository.findByUsername(principal.getName())
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Data User tidak ditemukan di database."));
    }

    @PostMapping
    public ResponseEntity<?> createKomentar(@RequestBody final KomentarRequest request, final Principal principal) {
        try {
            request.setPelajarId(getUserId(principal));
            Komentar saved = komentarService.createKomentar(request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            // Mengembalikan pesan error spesifik agar terbaca oleh Frontend
            return ResponseEntity.badRequest().body("Gagal menyimpan: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateKomentar(@PathVariable final Long id, @RequestBody final KomentarRequest request, final Principal principal) {
        try {
            return ResponseEntity.ok(komentarService.updateKomentar(id, getUserId(principal), request.getIsi()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal mengedit: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKomentar(@PathVariable final Long id, final Principal principal) {
        try {
            komentarService.deleteKomentar(id, getUserId(principal));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal menghapus: " + e.getMessage());
        }
    }

    @GetMapping("/bacaan/{bacaanId}")
    public ResponseEntity<List<Komentar>> getKomentarByBacaanId(@PathVariable final Long bacaanId) {
        return ResponseEntity.ok(komentarService.getKomentarUtamaByBacaanId(bacaanId));
    }

    @PostMapping("/{id}/reaksi")
    public ResponseEntity<?> toggleReaksi(
            @PathVariable final Long id,
            @RequestBody final ReaksiRequest request,
            final Principal principal) {
        try {
            komentarService.toggleReaksi(id, getUserId(principal), request.getJenisReaksi());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Gagal memberikan reaksi: " + e.getMessage());
        }
    }
}
