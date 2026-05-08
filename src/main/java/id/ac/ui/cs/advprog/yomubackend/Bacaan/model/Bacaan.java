package id.ac.ui.cs.advprog.yomubackend.Bacaan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bacaan")
public class Bacaan {
    /** ID dari bacaan. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Judul bacaan. */
    @Column(nullable = false)
    private String judul;

    /** Konten teks bacaan. */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String konten;
    /** menghapus teks bacaan. */
    @jakarta.persistence.OneToMany(mappedBy = "bacaan", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Pertanyaan> daftarPertanyaan;
}
