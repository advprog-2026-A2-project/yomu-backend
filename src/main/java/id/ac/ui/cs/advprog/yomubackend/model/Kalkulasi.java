package id.ac.ui.cs.advprog.yomubackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

/**
 * Entity representing a kalkulasi result.
 */
@Entity
@Data
public class Kalkulasi {

    /**
     * Primary key identifier.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int angkaPertama;
    private int angkaKedua;
    private int hasil;
}
