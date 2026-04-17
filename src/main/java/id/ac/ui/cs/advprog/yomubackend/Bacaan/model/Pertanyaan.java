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

/** Model untuk data Pertanyaan kuis. */
@Getter
@Setter
@Entity
@Table(name = "pertanyaan")
public class Pertanyaan {

    /** ID unik pertanyaan. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Isi pertanyaan. */
    @Column(nullable = false)
    private String soal;

    /** Opsi jawaban A. */
    private String opsiA;
    /** Opsi jawaban B. */
    private String opsiB;
    /** Opsi jawaban C. */
    private String opsiC;
    /** Opsi jawaban D. */
    private String opsiD;

    /** Kunci jawaban yang benar. */
    @Column(nullable = false)
    private String kunciJawaban;

    /** Relasi ke bacaan. */
    @ManyToOne
    @JoinColumn(name = "bacaan_id", nullable = false)
    private Bacaan bacaan;
}
