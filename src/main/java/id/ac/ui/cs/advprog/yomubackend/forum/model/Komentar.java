package id.ac.ui.cs.advprog.yomubackend.forum.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entitas yang merepresentasikan komentar pada suatu bacaan.
 */
@Entity
@Table(name = "komentar")
@Getter
@Setter
@NoArgsConstructor
public class Komentar {

    /**
     * ID unik komentar.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID bacaan yang dikomentari (referensi dari modul lain).
     */
    @Column(nullable = false)
    private Long bacaanId;

    /**
     * ID pelajar yang memberikan komentar.
     */
    @Column(nullable = false)
    private Long pelajarId;

    /**
     * Isi teks dari komentar.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String isi;

    /**
     * Waktu pembuatan komentar.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Referensi ke komentar parent jika merupakan balasan.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Komentar parent;

    /**
     * Daftar balasan untuk komentar ini.
     */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Komentar> balasan = new ArrayList<>();
}