package id.ac.ui.cs.advprog.yomubackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import id.ac.ui.cs.advprog.yomubackend.model.Kalkulasi;
import id.ac.ui.cs.advprog.yomubackend.repository.KalkulasiRepository;

@Controller
public final class KalkulasiController {

    private final KalkulasiRepository repo;

    public KalkulasiController(final KalkulasiRepository repository) {
        this.repo = repository;
    }

    @GetMapping("/")
    public String tampilkanHalamanUtama(Model model) {
        // Ambil semua data riwayat dari database, lalu lempar ke file HTML
        model.addAttribute("riwayatKalkulasi", repo.findAll());
        return "index"; // Akan mencari file bernama "index.html"
    }

    @PostMapping("/hitung")
    public String lakukanPerhitungan(@RequestParam int angka1, @RequestParam int angka2) {
        int hasilTambah = angka1 + angka2;

        Kalkulasi dataBaru = new Kalkulasi();
        dataBaru.setAngkaPertama(angka1);
        dataBaru.setAngkaKedua(angka2);
        dataBaru.setHasil(hasilTambah);
        repo.save(dataBaru);

        return "redirect:/";
    }
}
