package id.ac.ui.cs.advprog.yomubackend.forum.repository;

import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KomentarRepository extends JpaRepository<Komentar, Long> {
    // Mengambil semua komentar level teratas (yang bukan balasan) pada suatu bacaan
    List<Komentar> findByBacaanIdAndParentIsNullOrderByCreatedAtAsc(Long bacaanId);
}