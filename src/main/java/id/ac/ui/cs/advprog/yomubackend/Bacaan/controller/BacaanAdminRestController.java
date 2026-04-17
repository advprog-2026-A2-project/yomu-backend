package id.ac.ui.cs.advprog.yomubackend.Bacaan.controller;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/bacaan")
@RequiredArgsConstructor
public class BacaanAdminRestController {

    private final BacaanRepository bacaanRepository;
    private final PertanyaanRepository pertanyaanRepository;

    // Langkah 3: Buat teks baru
    @PostMapping("/create")
    public ResponseEntity<?> createBacaan(@RequestBody Bacaan bacaan) {
        return ResponseEntity.ok(bacaanRepository.save(bacaan));
    }

    // Langkah 4: Hapus teks (Otomatis hapus kuis karena Cascade di model)
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBacaan(@PathVariable Long id) {
        if (!bacaanRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Bacaan tidak ditemukan");
        }
        bacaanRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Bacaan dan kuis terkait berhasil dihapus"));
    }

    // Langkah 5: Tambah pertanyaan kuis ke bacaan tertentu
    @PostMapping("/{id}/add-pertanyaan")
    public ResponseEntity<?> addPertanyaan(@PathVariable Long id, @RequestBody Pertanyaan p) {
        return bacaanRepository.findById(id).map(bacaan -> {
            p.setBacaan(bacaan);
            pertanyaanRepository.save(p);
            return ResponseEntity.ok(Map.of("message", "Pertanyaan berhasil ditambahkan"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // API pendukung untuk list di halaman admin
    @GetMapping("/list")
    public List<Bacaan> listSemua() {
        return bacaanRepository.findAll();
    }
    // Tambahkan ini di dalam BacaanAdminRestController.java

    // Ambil semua pertanyaan buat satu bacaan (biar admin bisa liat listnya)
    @GetMapping("/{id}/questions")
    public ResponseEntity<List<Pertanyaan>> getQuestionsByBacaan(@PathVariable Long id) {
        return ResponseEntity.ok(pertanyaanRepository.findByBacaanId(id));
    }

    // Update Pertanyaan (Edit Soal/Kunci)
    @PutMapping("/question/{qId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long qId, @RequestBody Pertanyaan p) {
        return pertanyaanRepository.findById(qId).map(existing -> {
            existing.setSoal(p.getSoal());
            existing.setOpsiA(p.getOpsiA());
            existing.setOpsiB(p.getOpsiB());
            existing.setOpsiC(p.getOpsiC());
            existing.setOpsiD(p.getOpsiD());
            existing.setKunciJawaban(p.getKunciJawaban());
            pertanyaanRepository.save(existing);
            return ResponseEntity.ok(Map.of("message", "Pertanyaan berhasil diupdate"));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Hapus satu pertanyaan saja
    @DeleteMapping("/question/{qId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long qId) {
        if (!pertanyaanRepository.existsById(qId)) {
            return ResponseEntity.badRequest().body("Pertanyaan tidak ditemukan");
        }
        pertanyaanRepository.deleteById(qId);
        return ResponseEntity.ok(Map.of("message", "Pertanyaan berhasil dihapus"));
    }
}