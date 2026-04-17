package id.ac.ui.cs.advprog.yomubackend.forum.repository;

import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import id.ac.ui.cs.advprog.yomubackend.forum.model.Reaksi;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReaksiRepository extends JpaRepository<Reaksi, Long> {

    // Mencari apakah user sudah melakukan upvote atau downvote
    Optional<Reaksi> findByKomentarAndPelajarIdAndJenisReaksiIn(Komentar komentar, String pelajarId, List<String> jenisReaksi);

    // Mencari reaksi emoji spesifik
    Optional<Reaksi> findByKomentarAndPelajarIdAndJenisReaksi(Komentar komentar, String pelajarId, String jenisReaksi);
}
