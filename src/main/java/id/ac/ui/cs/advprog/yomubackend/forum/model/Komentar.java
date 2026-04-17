package id.ac.ui.cs.advprog.yomubackend.forum.model;

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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "komentar")
@Getter
@Setter
@NoArgsConstructor
public class Komentar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long bacaanId;

    // Pastikan ini sudah String
    @Column(nullable = false)
    private String pelajarId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String isi;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    private Komentar parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Komentar> balasan = new ArrayList<>();

    /**
     * Daftar reaksi (Upvote/Downvote/Emoji) yang ada pada komentar ini.
     */
    @OneToMany(mappedBy = "komentar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaksi> daftarReaksi = new ArrayList<>();
}