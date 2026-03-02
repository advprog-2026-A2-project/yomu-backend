package id.ac.ui.cs.advprog.yomubackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import id.ac.ui.cs.advprog.yomubackend.model.Kalkulasi;
import id.ac.ui.cs.advprog.yomubackend.repository.KalkulasiRepository;

/**
 * Controller for kalkulasi-related web requests.
 */
@Controller
public final class KalkulasiController {

    /**
     * Repository for accessing kalkulasi data.
     */
    private final KalkulasiRepository repo;

    /**
     * Constructs a KalkulasiController.
     *
     * @param repository repository for kalkulasi entities
     */
    public KalkulasiController(final KalkulasiRepository repository) {
        this.repo = repository;
    }

    /**
     * Displays the main page with kalkulasi history.
     *
     * @param model Spring MVC model
     * @return name of the index view
     */
    @GetMapping("/")
    public String tampilkanHalamanUtama(final Model model) {
        model.addAttribute("riwayatKalkulasi", repo.findAll());
        return "index";
    }

    /**
     * Performs addition and saves the result.
     *
     * @param angka1 first operand
     * @param angka2 second operand
     * @return redirect to main page
     */
    @PostMapping("/hitung")
    public String lakukanPerhitungan(
            @RequestParam final int angka1,
            @RequestParam final int angka2
    ) {
        final int hasilTambah = angka1 + angka2;

        final Kalkulasi dataBaru = new Kalkulasi();
        dataBaru.setAngkaPertama(angka1);
        dataBaru.setAngkaKedua(angka2);
        dataBaru.setHasil(hasilTambah);

        repo.save(dataBaru);
        return "redirect:/";
    }
}
