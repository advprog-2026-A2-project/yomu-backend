package id.ac.ui.cs.advprog.yomubackend.Bacaan.repository;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PertanyaanRepository extends JpaRepository<Pertanyaan, Long> {
    /**
     * Mencari daftar pertanyaan berdasarkan ID bacaan.
     * @param bacaanId ID dari bacaan
     * @return List berisi pertanyaan terkait
     */
    List<Pertanyaan> findByBacaanId(Long bacaanId);
}
