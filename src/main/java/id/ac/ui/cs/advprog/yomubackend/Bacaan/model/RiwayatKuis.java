package id.ac.ui.cs.advprog.yomubackend.Bacaan.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "riwayat_kuis")
public class RiwayatKuis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username; // Menyimpan nama user/email yang lagi login

    @ManyToOne
    @JoinColumn(name = "bacaan_id", nullable = false)
    private Bacaan bacaan; // Menyimpan kuis apa yang dikerjain

    private int nilai; // Nyatet nilainya juga sekalian
}