package id.ac.ui.cs.advprog.yomubackend.Bacaan.controller;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.BacaanRepository;
import id.ac.ui.cs.advprog.yomubackend.Bacaan.repository.PertanyaanRepository;
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

    // 1. Menampilkan Daftar Bacaan (Bisa ditaruh di homepage atau /bacaan)
    @GetMapping("/")
    public String tampilkanDaftarBacaan(Model model) {
        List<Bacaan> daftarBacaan = bacaanRepository.findAll();
        model.addAttribute("daftarBacaan", daftarBacaan);
        return "index";
    }

    // 2. Menampilkan detail teks saat judulnya diklik
    @GetMapping("/bacaan/{id}")
    public String bacaTeks(@PathVariable Long id, Model model) {
        Bacaan bacaan = bacaanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bacaan tidak ditemukan"));
        model.addAttribute("bacaan", bacaan);
        return "bacaan_detail";
    }
    @GetMapping("/bacaan/{id}/kuis")
    public String kerjakanKuis(@PathVariable Long id, Model model) {

        Bacaan bacaan = bacaanRepository.findById(id).orElseThrow();
        model.addAttribute("judulBacaan", bacaan.getJudul());
        model.addAttribute("bacaanId", bacaan.getId());


        List<Pertanyaan> daftarSoal = pertanyaanRepository.findByBacaanId(id);
        model.addAttribute("daftarSoal", daftarSoal);

        return "kuis_halaman"; // Kita bikin html ini habis ini
    }

    @PostMapping("/submit-kuis")
    public String prosesKuis(@RequestParam("bacaanId") Long bacaanId,
                             @RequestParam Map<String, String> semuaJawaban,
                             Model model) {

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


        model.addAttribute("nilai", nilaiAkhir);
        model.addAttribute("benar", jumlahBenar);
        model.addAttribute("total", totalSoal);

        return "kuis_hasil"; // Lanjut bikin file HTML ini
    }
}