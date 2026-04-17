package id.ac.ui.cs.advprog.yomubackend.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reaksi_komentar")
@Getter
@Setter
@NoArgsConstructor
public class Reaksi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pelajarId;

    @Column(nullable = false)
    private String jenisReaksi; // Akan berisi "UPVOTE", "DOWNVOTE", atau Emoji ("🔥", dll)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "komentar_id", nullable = false)
    @JsonIgnore // Mencegah infinite loop saat diubah ke JSON
    private Komentar komentar;
}