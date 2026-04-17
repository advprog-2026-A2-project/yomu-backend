package id.ac.ui.cs.advprog.yomubackend.Bacaan.repository;

import id.ac.ui.cs.advprog.yomubackend.Bacaan.model.Bacaan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BacaanRepository extends JpaRepository<Bacaan, Long> {
}