package id.ac.ui.cs.advprog.yomubackend.Bacaan.repository;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiwayatKuisRepository extends JpaRepository<RiwayatKuis, Long> {

    // Fungsi sakti buat ngecek: Apakah ada record dengan username X dan bacaan_id Y?
    boolean existsByUsernameAndBacaanId(String username, Long bacaanId);

    // Opsional: Buat ngambil data nilainya kalau mau ditampilin lagi
    RiwayatKuis findByUsernameAndBacaanId(String username, Long bacaanId);
}
