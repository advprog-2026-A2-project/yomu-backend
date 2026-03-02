package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.model.Kalkulasi;
import id.ac.ui.cs.advprog.yomubackend.repository.KalkulasiRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class KalkulasiController {

    private final KalkulasiRepository repository;

    public KalkulasiController(KalkulasiRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String tampilkanHalamanUtama(Model model) {
        // Ambil semua data riwayat dari database, lalu lempar ke file HTML
        model.addAttribute("riwayatKalkulasi", repository.findAll());
        return "index"; // Akan mencari file bernama "index.html"
    }

    @PostMapping("/hitung")
    public String lakukanPerhitungan(@RequestParam int angka1, @RequestParam int angka2) {
        int hasilTambah = angka1 + angka2;

        Kalkulasi dataBaru = new Kalkulasi();
        dataBaru.setAngkaPertama(angka1);
        dataBaru.setAngkaKedua(angka2);
        dataBaru.setHasil(hasilTambah);
        repository.save(dataBaru);

        return "redirect:/";
    }
}