package id.ac.ui.cs.advprog.yomubackend.forum.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entitas yang merepresentasikan komentar pada suatu bacaan.
 */
@Entity
@Table(name = "komentar")
@Getter
@Setter
@NoArgsConstructor
public class Komentar {

    /** ID unik komentar. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** ID bacaan yang dikomentari (referensi dari modul lain). */
    @Column(nullable = false)
    private Long bacaanId;

    /** ID pelajar yang memberikan komentar. */
    @Column(nullable = false)
    private String pelajarId;

    /** Isi teks komentar. */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String isi;

    /** Waktu pembuatan komentar. */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /** Komentar parent jika ini adalah balasan. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Komentar parent;

    /** Daftar balasan untuk komentar ini. */
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Komentar> balasan = new ArrayList<>();

    /** Daftar reaksi pada komentar ini. */
    @OneToMany(mappedBy = "komentar", cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Reaksi> daftarReaksi = new ArrayList<>();
}

