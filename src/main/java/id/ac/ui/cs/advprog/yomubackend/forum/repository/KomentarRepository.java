package id.ac.ui.cs.advprog.yomubackend.forum.repository;

import id.ac.ui.cs.advprog.yomubackend.forum.model.Komentar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface untuk mengakses data entitas Komentar.
 */
@Repository
public interface KomentarRepository extends JpaRepository<Komentar, Long> {

    /**
     * Mengambil komentar utama (tanpa parent) berdasarkan bacaanId.
     *
     * @param bacaanId ID dari bacaan terkait
     * @return List komentar utama yang diurutkan berdasarkan waktu pembuatan
     */
    List<Komentar> findByBacaanIdAndParentIsNullOrderByCreatedAtAsc(
            Long bacaanId);
}

