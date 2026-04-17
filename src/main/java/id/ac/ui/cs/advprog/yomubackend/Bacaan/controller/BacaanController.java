package id.ac.ui.cs.advprog.yomubackend.Bacaan.controller;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Controller untuk mengelola bacaan dan kuis. */
@Controller
public class BacaanController {

    /** Repository bacaan. */
    @Autowired private BacaanRepository bacaanRepository;
    /** Repository pertanyaan. */
    @Autowired private PertanyaanRepository pertanyaanRepository;
    /** Repository riwayat. */
    @Autowired private RiwayatKuisRepository riwayatKuisRepository;

    /** Menampilkan daftar bacaan. */
    @GetMapping("/")
    public String tampilkanDaftarBacaan(final Model model) {
        List<Bacaan> daftarBacaan = bacaanRepository.findAll();
        model.addAttribute("daftarBacaan", daftarBacaan);
        return "index";
    }

    /** Menampilkan detail teks. */
    @GetMapping("/bacaan/{id}")
    public String bacaTeks(@PathVariable final Long id, final Model model,
                           final Principal principal) {
        Bacaan bacaan = bacaanRepository.findById(id).orElseThrow();
        model.addAttribute("bacaan", bacaan);
        boolean sudahDikerjakan = false;
        if (principal != null) {
            String username = principal.getName();
            sudahDikerjakan = riwayatKuisRepository.existsByUsernameAndBacaanId(
                    username, id);
            if (sudahDikerjakan) {
                RiwayatKuis riwayat = riwayatKuisRepository
                        .findByUsernameAndBacaanId(username, id);
                model.addAttribute("nilaiSebelumnya", riwayat.getNilai());
            }
        }
        model.addAttribute("sudahDikerjakan", sudahDikerjakan);
        return "bacaan_detail";
    }

    /** Menampilkan halaman kuis. */
    @GetMapping("/bacaan/{id}/kuis")
    public String kerjakanKuis(@PathVariable final Long id, final Model model,
                               final Principal principal) {
        if (principal != null) {
            boolean sudahDikerjakan = riwayatKuisRepository.existsByUsernameAndBacaanId(
                    principal.getName(), id);
            if (sudahDikerjakan) {
                return "redirect:/bacaan/" + id;
            }
        }
        Bacaan bacaan = bacaanRepository.findById(id).orElseThrow();
        model.addAttribute("judulBacaan", bacaan.getJudul());
        model.addAttribute("bacaanId", bacaan.getId());
        List<Pertanyaan> daftarSoal = pertanyaanRepository.findByBacaanId(id);
        model.addAttribute("daftarSoal", daftarSoal);
        return "kuis_halaman";
    }

    /** Memproses jawaban kuis. */
    @PostMapping("/submit-kuis")
    public String prosesKuis(@RequestParam("bacaanId") final Long bacaanId,
                             @RequestParam final Map<String, String> semuaJawaban,
                             final Model model, final Principal principal) {
        int jumlahBenar = 0;
        int totalSoal = 0;
        List<Pertanyaan> daftarSoal = pertanyaanRepository.findByBacaanId(bacaanId);
        for (Pertanyaan soal : daftarSoal) {
            totalSoal++;
            String jawabanUser = semuaJawaban.get("jawaban_" + soal.getId());
            if (jawabanUser != null && jawabanUser.equals(soal.getKunciJawaban())) {
                jumlahBenar++;
            }
        }
        final int seratus = 100;
        int nilaiAkhir = (totalSoal == 0) ? 0 : (jumlahBenar * seratus) / totalSoal;
        if (principal != null) {
            String username = principal.getName();
            if (!riwayatKuisRepository.existsByUsernameAndBacaanId(username,
                    bacaanId)) {
                RiwayatKuis riwayatBaru = new RiwayatKuis();
                riwayatBaru.setUsername(username);
                riwayatBaru.setBacaan(bacaanRepository.findById(bacaanId).orElseThrow());
                riwayatBaru.setNilai(nilaiAkhir);
                riwayatKuisRepository.save(riwayatBaru);
            }
        }
        model.addAttribute("nilai", nilaiAkhir);
        model.addAttribute("benar", jumlahBenar);
        model.addAttribute("total", totalSoal);
        return "kuis_hasil";
    }
}
// Baris kosong di sini penting!