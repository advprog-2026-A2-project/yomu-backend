package id.ac.ui.cs.advprog.yomubackend.Bacaan.controller;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.RiwayatKuisRepository;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BacaanController {

    @Autowired
    private BacaanRepository bacaanRepository;
    @Autowired
    private PertanyaanRepository pertanyaanRepository;
    @Autowired
    private RiwayatKuisRepository riwayatKuisRepository;

    // 1. Menampilkan Daftar Bacaan (Bisa ditaruh di homepage atau /bacaan)
    @GetMapping("/")
    public String tampilkanDaftarBacaan(Model model) {
        List<Bacaan> daftarBacaan = bacaanRepository.findAll();
        model.addAttribute("daftarBacaan", daftarBacaan);
        return "index";
    }

    // 2. Menampilkan detail teks saat judulnya diklik
    @GetMapping("/bacaan/{id}")
    public String bacaTeks(@PathVariable Long id, Model model, Principal principal) {
        Bacaan bacaan = bacaanRepository.findById(id).orElseThrow();
        model.addAttribute("bacaan", bacaan);

        // Cek apakah user udah pernah ngerjain
        boolean sudahDikerjakan = false;
        if (principal != null) { // Pastikan user udah login
            String username = principal.getName();
            sudahDikerjakan = riwayatKuisRepository.existsByUsernameAndBacaanId(username, id);

            // Kalau udah ngerjain, kita kirim nilainya juga ke layar
            if(sudahDikerjakan){
                RiwayatKuis riwayat = riwayatKuisRepository.findByUsernameAndBacaanId(username, id);
                model.addAttribute("nilaiSebelumnya", riwayat.getNilai());
            }
        }
        model.addAttribute("sudahDikerjakan", sudahDikerjakan);

        return "bacaan_detail";
    }
    @GetMapping("/bacaan/{id}/kuis")
    public String kerjakanKuis(@PathVariable Long id, Model model, Principal principal) {
        if (principal != null) {
            boolean sudahDikerjakan = riwayatKuisRepository.existsByUsernameAndBacaanId(principal.getName(), id);
            if (sudahDikerjakan) {
                return "redirect:/bacaan/" + id; // Usir balik ke halaman teks
            }
        }


        Bacaan bacaan = bacaanRepository.findById(id).orElseThrow();
        model.addAttribute("judulBacaan", bacaan.getJudul());
        model.addAttribute("bacaanId", bacaan.getId());
        List<Pertanyaan> daftarSoal = pertanyaanRepository.findByBacaanId(id);
        model.addAttribute("daftarSoal", daftarSoal);

        return "kuis_halaman";
    }

    @PostMapping("/submit-kuis")
    public String prosesKuis(@RequestParam("bacaanId") Long bacaanId,
                             @RequestParam Map<String, String> semuaJawaban,
                             Model model, Principal principal) { // Tambah Principal disini


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
        int nilaiAkhir = (totalSoal == 0) ? 0 : (jumlahBenar * 100) / totalSoal;


        if (principal != null) {
            String username = principal.getName();
            if (!riwayatKuisRepository.existsByUsernameAndBacaanId(username, bacaanId)) {
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