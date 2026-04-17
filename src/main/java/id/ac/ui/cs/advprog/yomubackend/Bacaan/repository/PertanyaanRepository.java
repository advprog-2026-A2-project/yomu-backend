package id.ac.ui.cs.advprog.yomubackend.Bacaan.repository;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Pertanyaan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PertanyaanRepository extends JpaRepository<Pertanyaan, Long> {
    List<Pertanyaan> findByBacaanId(Long bacaanId);
}