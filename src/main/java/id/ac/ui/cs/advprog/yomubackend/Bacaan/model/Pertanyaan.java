package id.ac.ui.cs.advprog.yomubackend.Bacaan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pertanyaan")
public class Pertanyaan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String soal;

    private String opsiA;
    private String opsiB;
    private String opsiC;
    private String opsiD;

    @Column(nullable = false)
    private String kunciJawaban;

    @ManyToOne
    @JoinColumn(name = "bacaan_id", nullable = false)
    private Bacaan bacaan;
}