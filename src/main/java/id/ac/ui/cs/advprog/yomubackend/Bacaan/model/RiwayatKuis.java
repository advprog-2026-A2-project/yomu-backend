package id.ac.ui.cs.advprog.yomubackend.Bacaan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/** Model untuk menyimpan riwayat kuis user. */
@Getter
@Setter
@Entity
@Table(name = "riwayat_kuis")
public class RiwayatKuis {

    /** ID unik riwayat. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username user. */
    @Column(nullable = false)
    private String username;

    /** Bacaan terkait. */
    @ManyToOne
    @JoinColumn(name = "bacaan_id", nullable = false)
    private Bacaan bacaan;

    /** Nilai yang diperoleh. */
    private int nilai;
}
