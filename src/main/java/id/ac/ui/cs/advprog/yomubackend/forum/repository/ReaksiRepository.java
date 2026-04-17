package id.ac.ui.cs.advprog.yomubackend.forum.repository;

import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.model.Reaksi;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository untuk mengelola reaksi pada komentar.
 */
@Repository
public interface ReaksiRepository extends JpaRepository<Reaksi, Long> {

    /**
     * Mencari reaksi upvote atau downvote dari user pada komentar.
     *
     * @param komentar komentar yang direaksi
     * @param pelajarId ID pelajar yang memberikan reaksi
     * @param jenisReaksi daftar jenis reaksi yang dicari
     * @return optional reaksi jika ditemukan
     */
    Optional<Reaksi> findByKomentarAndPelajarIdAndJenisReaksiIn(
            Komentar komentar, String pelajarId,
            List<String> jenisReaksi);

    /**
     * Mencari reaksi emoji spesifik dari user pada komentar.
     *
     * @param komentar komentar yang direaksi
     * @param pelajarId ID pelajar yang memberikan reaksi
     * @param jenisReaksi jenis reaksi yang dicari
     * @return optional reaksi jika ditemukan
     */
    Optional<Reaksi> findByKomentarAndPelajarIdAndJenisReaksi(
            Komentar komentar, String pelajarId, String jenisReaksi);
}

