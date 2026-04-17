package id.ac.ui.cs.advprog.yomubackend.Bacaan.repository;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.RiwayatKuis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiwayatKuisRepository extends JpaRepository<RiwayatKuis, Long> {

    /**
     * Mengecek apakah ada record dengan username dan bacaan id tertentu.
     * @param username nama pengguna
     * @param bacaanId ID dari bacaan
     * @return boolean true jika sudah ada, false jika belum
     */
    boolean existsByUsernameAndBacaanId(String username, Long bacaanId);

    /**
     * Mengambil data riwayat kuis berdasarkan username dan bacaan id.
     * @param username nama pengguna
     * @param bacaanId ID dari bacaan
     * @return RiwayatKuis data riwayat kuis terkait
     */
    RiwayatKuis findByUsernameAndBacaanId(String username, Long bacaanId);
}