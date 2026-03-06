package id.ac.ui.cs.advprog.yomubackend.forum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "komentar")
@Getter
@Setter
@NoArgsConstructor
public class Komentar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Referensi ke Modul Bacaan (Independen)
    @Column(nullable = false)
    private Long bacaanId;

    // Referensi ke Modul Autentikasi (Independen)
    @Column(nullable = false)
    private Long pelajarId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String isi;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Relasi untuk komentar parent (jika ini adalah balasan)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Komentar parent;

    // Relasi untuk mengambil semua balasan dari komentar ini (Nested)
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Komentar> balasan = new ArrayList<>();
}